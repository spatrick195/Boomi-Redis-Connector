package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseDeleteOperation;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisCommands;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.StringUtils;

import static nz.co.fmg.redis.Utils.StringConstants.*;

public class RedisDeleteOperation extends BaseDeleteOperation {
    private final RedisCommands redis;

    public RedisDeleteOperation(RedisConnection conn) {
        super(conn);
        redis = conn.getRedisInstance();
    }

    @Override
    protected void executeDelete(DeleteRequest request, OperationResponse response) {
        for (ObjectIdData objectIdData : request) {
            try {
                String cacheKey = BoomiUtils.GetPrefixedKey(objectIdData, "cacheKey");
                String cacheInnerKey = BoomiUtils.GetDynamicProperty(objectIdData, "cacheInnerKey");

                long deletedValue;
                boolean isHash = StringUtils.isNotEmpty(cacheInnerKey);
                boolean deleteByPattern = BoomiUtils.GetOperationBoolProperty(getContext(), "deleteByPattern");

                if (StringUtils.isEmpty(cacheKey)) {
                    response.getLogger().severe("REDIS: Delete operation failed because the cache key was empty.");
                    response.addResult(objectIdData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_NO_KEY, "The key pattern is a required document property", null);
                }

                if (deleteByPattern) {
                    deletedValue = isHash ? redis.hashPattern(cacheKey) : redis.pattern(cacheKey);
                } else {
                    deletedValue = isHash ? redis.delete(cacheKey, cacheInnerKey) : redis.delete(cacheKey);
                }
                response.getLogger().fine(String.format("REDIS: Attempting to delete '%s' key(s)", deletedValue));
                if (deletedValue > 0) {
                    response.addResult(objectIdData, OperationStatus.SUCCESS, RESPONSE_SUCCESS, RESPONSE_SUCCESS, PayloadUtil.toPayload(RESPONSE_SUCCESS));
                } else if (deletedValue == 0) {
                    response.addResult(objectIdData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_NO_KEY, "No keys deleted", PayloadUtil.toPayload(String.format("Deleted %s key(s)", deletedValue)));
                } else {
                    ResponseUtil.addEmptyFailure(response, objectIdData, RESPONSE_FAIL_ERROR);
                }
            } catch (Exception e) {
                response.addErrorResult(objectIdData, OperationStatus.FAILURE, RESPONSE_FAIL_ERROR,
                        e.getMessage(), e);
            }
        }
    }

}

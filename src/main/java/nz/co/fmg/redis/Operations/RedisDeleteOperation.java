package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseDeleteOperation;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisRepository;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.StringUtils;

import java.util.logging.Logger;

import static nz.co.fmg.redis.Utils.Constants.*;

public class RedisDeleteOperation extends BaseDeleteOperation {
    private final RedisRepository redis;

    public RedisDeleteOperation(RedisConnection conn) {
        super(conn);
        redis = conn.getRedisInstance();
    }

    @Override
    protected void executeDelete(DeleteRequest request, OperationResponse response) {
        Logger logger = response.getLogger();
        for (ObjectIdData objectIdData : request) {
            try {
                String cacheKey = BoomiUtils.GetPrefixedKey(objectIdData, "cacheKey");
                String cacheField = BoomiUtils.GetDynamicProperty(objectIdData, "cacheInnerKey");

                long deletedValue;
                boolean isSuccessful;
                boolean isHash = StringUtils.isNotEmpty(cacheField);
                boolean deleteByPattern = BoomiUtils.GetOperationBoolProperty(getContext(), "deleteByPattern");

                if (StringUtils.isEmpty(cacheKey)) {
                    logger.severe("REDIS: Delete operation failed because the cache key was empty.");
                    response.addErrorResult(objectIdData, OperationStatus.APPLICATION_ERROR, HTTP_400, BAD_REQUEST, new Exception("The key is a required document property"));
                }

                logger.info(String.format("REDIS: Deleting key '%s' \n Field: '%s'", cacheKey, cacheField));

                if (deleteByPattern) {
                    deletedValue = isHash ? redis.keys(cacheKey) : redis.pattern(cacheKey);
                } else {
                    deletedValue = isHash ? redis.delete(cacheKey, cacheField) : redis.delete(cacheKey);
                }

                isSuccessful = deletedValue > HASH_RESPONSE_FAILURE;
                logger.info(String.format("REDIS: Deleted '%s' key(s)", deletedValue));

                if (isSuccessful) {
                    response.addResult(objectIdData, OperationStatus.SUCCESS, OK, OK, PayloadUtil.toPayload(HASH_RESPONSE_SUCCESS));
                } else {
                    response.addEmptyResult(objectIdData, OperationStatus.APPLICATION_ERROR, HTTP_404, NOT_FOUND);
                }

            } catch (Exception e) {
                logger.severe(String.format("REDIS: An unexpected error occurred during the execution:\n %s",
                        e.getMessage()));
                response.addErrorResult(objectIdData, OperationStatus.FAILURE, HTTP_500, INTERNAL_ERROR, e);
            }
        }
    }

}

package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseUpdateOperation;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisRepository;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.StringUtils;

import static nz.co.fmg.redis.Utils.Constants.*;

public class RedisCreateOperation extends BaseUpdateOperation {
    private final RedisRepository redis;

    public RedisCreateOperation(RedisConnection connection) {
        super(connection);
        redis = connection.getRedisInstance();
    }

    @Override
    protected void executeUpdate(UpdateRequest request, OperationResponse response) {
        for (ObjectData objectData : request) {
            try {
                String result;
                boolean isSuccessful;

                String cacheKey = BoomiUtils.GetPrefixedKey(objectData, "cacheKey");
                response.getLogger().info(String.format("REDIS: Cache key set to '%s'", cacheKey));

                String cacheExpiry = BoomiUtils.GetDynamicProperty(objectData, "cacheExpiry");
                if (StringUtils.isNotEmpty(cacheExpiry)) {
                    response.getLogger().info(String.format("REDIS: Cache expiry set to '%s'", cacheExpiry));
                } else {
                    response.addErrorResult(objectData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_ERROR,
                            RESPONSE_FAIL_NO_KEY, new Exception("No key specified"));
                }

                String cacheField = BoomiUtils.GetDynamicProperty(objectData, "cacheInnerKey");
                if (StringUtils.isNotEmpty(cacheField)) {
                    response.getLogger().info(String.format("REDIS: Cache field set to '%s'", cacheField));
                }

                String cacheValue = BoomiUtils.GetDynamicProperty(objectData, "cacheValue");
                response.getLogger().info(String.format("REDIS: Cache value set to '%s'", cacheValue));

                boolean isHash = StringUtils.isNotEmpty(cacheField);

                if (StringUtils.isEmpty(cacheValue)) {
                    response.addErrorResult(objectData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_ERROR,
                            RESPONSE_FAIL_NO_KEY, new Exception("No value specified"));
                }
                response.getLogger().info("REDIS: Cache value has been specified. Using property instead of input stream");
                if (isHash) {
                    response.getLogger().info("REDIS: Cache field has been specified. Using hash set.");
                    result = redis.set(cacheKey, cacheField, cacheValue).toString();
                    isSuccessful = result.equals(HASH_RESPONSE_SUCCESS);
                } else if (StringUtils.isNotEmpty(cacheExpiry)) {
                    response.getLogger().info("REDIS: Cache expiry has been specified. Creating key with expiry.");
                    result = redis.set(cacheKey, Integer.parseInt(cacheExpiry), cacheValue);
                    isSuccessful = result.equals(RESPONSE_SUCCESS);
                } else {
                    result = redis.set(cacheKey, cacheValue);
                    isSuccessful = result.equals(RESPONSE_SUCCESS);
                }
                response.getLogger().fine(String.format("REDIS: 'SET %s' returned '%s'", cacheKey, result));

                if (isSuccessful) {
                    response.addResult(objectData, OperationStatus.SUCCESS, RESPONSE_SUCCESS, RESPONSE_SUCCESS,
                            PayloadUtil.toPayload(result));
                    return;
                } else {
                    response.getLogger().warning(String.format("'SET %s' was not set. Response value was: '%s'.",
                            cacheKey, result));
                    response.addEmptyResult(objectData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_ERROR,
                            RESPONSE_FAIL_ERROR);
                }
            } catch (Exception e) {
                response.getLogger().severe(String.format("REDIS: An unexpected error has occurred: '%s'", e.getMessage()));
                response.addErrorResult(objectData, OperationStatus.FAILURE, RESPONSE_FAIL_ERROR, e.getMessage(), e);
            }
        }
    }
}

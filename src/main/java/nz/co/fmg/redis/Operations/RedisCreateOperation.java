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

    public RedisCreateOperation(RedisConnection conn) {
        super(conn);
        redis = conn.getRedisInstance();
    }

    @Override
    protected void executeUpdate(UpdateRequest request, OperationResponse response) {
        for (ObjectData objectData : request) {
            try {
                String result;
                boolean isSuccessful;

                String cacheKey = BoomiUtils.GetPrefixedKey(objectData, "cacheKey");
                response.getLogger().info(String.format("REDIS: Cache key set to '%s'", cacheKey));
                if(StringUtils.isEmpty(cacheKey)){
                    response.addErrorResult(objectData, OperationStatus.APPLICATION_ERROR, HTTP_400,
                            BAD_REQUEST, new Exception("The cache key is a required document property"));

                }
                String cacheField = BoomiUtils.GetDynamicProperty(objectData, "cacheInnerKey");
                if (StringUtils.isNotEmpty(cacheField)) {
                    response.getLogger().info(String.format("REDIS: Cache field set to '%s'", cacheField));
                }

                String cacheValue = BoomiUtils.GetDynamicProperty(objectData, "cacheValue");
                response.getLogger().info(String.format("REDIS: Cache value set to '%s'", cacheValue));
                if (StringUtils.isEmpty(cacheValue)) {
                    response.addErrorResult(objectData, OperationStatus.APPLICATION_ERROR, HTTP_400,
                            BAD_REQUEST, new Exception("The cache value is a required document property"));
                }

                String cacheExpiry = BoomiUtils.GetDynamicProperty(objectData, "cacheExpiry");
                if (StringUtils.isNotEmpty(cacheExpiry)) {
                    response.getLogger().fine(String.format("REDIS: Cache expiry set to '%s'", cacheExpiry));
                }

                boolean isHash = StringUtils.isNotEmpty(cacheField);
                boolean isDeprecatedExpiry = redis.isExpiry();
                long deprecatedExpiry = redis.getExpiry();
                String logMessage = String.format("REDIS: Creating key '%s' \n Field: '%s' \n Value: '%s' \n Expiry: '%s'", cacheKey, cacheField, cacheValue, deprecatedExpiry);

                if (isHash) {
                    response.getLogger().info(logMessage);
                    result = redis.set(cacheKey, cacheField, cacheValue).toString();
                    isSuccessful = result.equals(HASH_RESPONSE_SUCCESS);
                } else if (StringUtils.isNotEmpty(cacheExpiry)) {
                    response.getLogger().info(logMessage);
                    result = redis.set(cacheKey, Integer.parseInt(cacheExpiry), cacheValue);
                    isSuccessful = result.equals(OK);
                } else {
                    response.getLogger().info(logMessage);
                    result = redis.set(cacheKey, cacheValue);
                    isSuccessful = result.equals(OK);
                }

                if (isDeprecatedExpiry) {
                    response.getLogger().warning("REDIS: Using deprecated expiry method");
                    redis.expire(cacheKey, redis.getExpiry().intValue());
                }

                response.getLogger().fine(String.format("REDIS: 'SET %s' returned '%s'", cacheKey, result));

                if (isSuccessful) {
                    response.addResult(objectData, OperationStatus.SUCCESS, HTTP_201, CREATED, PayloadUtil.toPayload(result));
                } else {
                    response.getLogger().warning(String.format("REDIS: 'SET %s' was not set. Response value was: '%s'.",
                            cacheKey, result));
                    response.addEmptyResult(objectData, OperationStatus.APPLICATION_ERROR, HTTP_400, BAD_REQUEST);
                }
            } catch (Exception e) {
                response.getLogger().severe(String.format("REDIS: An unexpected error has occurred: '%s'", e.getMessage()));
                response.addErrorResult(objectData, OperationStatus.FAILURE, HTTP_500, INTERNAL_ERROR, e);
            }
        }
    }
}

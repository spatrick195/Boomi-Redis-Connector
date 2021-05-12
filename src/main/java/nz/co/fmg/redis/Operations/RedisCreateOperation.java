package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseUpdateOperation;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisRepository;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.StringUtils;

import java.util.logging.Logger;

import static nz.co.fmg.redis.Utils.Constants.*;

public class RedisCreateOperation extends BaseUpdateOperation {
    private final RedisRepository redis;

    public RedisCreateOperation(RedisConnection conn) {
        super(conn);
        redis = conn.getRedisInstance();
    }

    @Override
    protected void executeUpdate(UpdateRequest request, OperationResponse response) {
        Logger logger = response.getLogger();
        for (ObjectData objectData : request) {
            String result;
            boolean isSuccessful;

            String cacheKey = BoomiUtils.GetPrefixedKey(objectData, "cacheKey");
            if (StringUtils.isEmpty(cacheKey)) {
                logger.severe("REDIS: Delete operation failed because the cache key was empty.");
                response.addErrorResult(objectData, OperationStatus.APPLICATION_ERROR, HTTP_400, BAD_REQUEST, new Exception("The cache key is a required document property"));
            }

            String cacheField = BoomiUtils.GetDynamicProperty(objectData, "cacheInnerKey");
            String cacheValue = BoomiUtils.GetDynamicProperty(objectData, "cacheValue");

            if (StringUtils.isEmpty(cacheValue)) {
                logger.severe("REDIS: Delete operation failed because the cache value was empty.");
                response.addErrorResult(objectData, OperationStatus.APPLICATION_ERROR, HTTP_400, BAD_REQUEST, new Exception("The cache value is a required document property"));
            }

            String cacheExpiry = BoomiUtils.GetDynamicProperty(objectData, "cacheExpiry");

            boolean isHash = StringUtils.isNotEmpty(cacheField);
            boolean isDeprecatedExpiry = redis.isExpiry();
            long deprecatedExpiry = redis.getExpiry();
            try {
                logger.info(String.format("REDIS: Creating key '%s' \n Field: '%s' \n Value: '%s' \n Expiry: '%s'", cacheKey, cacheField, cacheValue, deprecatedExpiry));
                if (isHash) {
                    result = redis.set(cacheKey, cacheField, cacheValue).toString();
                    isSuccessful = result.equals(HASH_RESPONSE_SUCCESS);
                } else if (StringUtils.isNotEmpty(cacheExpiry)) {
                    result = redis.set(cacheKey, Integer.parseInt(cacheExpiry), cacheValue);
                    isSuccessful = result.equals(OK);
                } else {
                    result = redis.set(cacheKey, cacheValue);
                    isSuccessful = result.equals(OK);
                }

                if (isDeprecatedExpiry) {
                    logger.warning("REDIS: Using deprecated expiry method");
                    redis.expire(cacheKey, redis.getExpiry().intValue());
                }

                logger.fine(String.format("REDIS: 'SET %s' returned '%s'", cacheKey, result));

                if (isSuccessful) {
                    response.addResult(objectData, OperationStatus.SUCCESS, HTTP_201, CREATED, PayloadUtil.toPayload(result));
                } else {
                    logger.warning(String.format("REDIS: 'SET %s' was not set. Response value was: '%s'.", cacheKey, result));
                    response.addEmptyResult(objectData, OperationStatus.APPLICATION_ERROR, HTTP_400, BAD_REQUEST);
                }
            } catch (Exception e) {
                response.getLogger().severe(String.format("REDIS: An unexpected error has occurred: '%s'", e.getMessage()));
                response.addErrorResult(objectData, OperationStatus.FAILURE, HTTP_500, INTERNAL_ERROR, e);
            }
        }
    }
}

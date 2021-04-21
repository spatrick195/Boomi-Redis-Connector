package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseUpdateOperation;
import com.boomi.util.StreamUtil;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisCommands;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static nz.co.fmg.redis.Utils.StringConstants.*;

public class RedisCreateOperation extends BaseUpdateOperation {
    private final RedisCommands redis;

    public RedisCreateOperation(RedisConnection connection) {
        super(connection);
        redis = connection.getRedisInstance();
    }

    @Override
    protected void executeUpdate(UpdateRequest request, OperationResponse response) {
        for (ObjectData objectData : request) {
            try {
                String data;
                String result;
                boolean isSuccessful;

                String cacheKey = BoomiUtils.GetPrefixedKey(objectData, "cacheKey");
                response.getLogger().info(String.format("REDIS: Cache key set to '%s'", cacheKey));

                String cacheExpiry = BoomiUtils.GetDynamicProperty(objectData, "cacheExpiry");
                if (StringUtils.isNotEmpty(cacheExpiry)) {
                    response.getLogger().info(String.format("REDIS: Cache expiry set to '%s'", cacheExpiry));
                }

                String cacheField = BoomiUtils.GetDynamicProperty(objectData, "cacheInnerKey");
                if (StringUtils.isNotEmpty(cacheField)) {
                    response.getLogger().info(String.format("REDIS: Cache field set to '%s'", cacheField));
                }

                String cacheValue = BoomiUtils.GetDynamicProperty(objectData, "cacheValue");
                response.getLogger().info(String.format("REDIS: Cache value set to '%s'", cacheValue));

                boolean isHash = StringUtils.isNotEmpty(cacheField);

                if (StringUtils.isNotEmpty(cacheValue)) {
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
                        response.getLogger().fine("REDIS: Created successfully");
                        response.addResult(objectData, OperationStatus.SUCCESS, RESPONSE_SUCCESS, RESPONSE_SUCCESS,
                                PayloadUtil.toPayload(result));
                        return;
                    } else {
                        response.getLogger().warning(String.format("'SET %s' was not set. Response value was: '%s'.",
                                cacheKey, result));
                        response.addEmptyResult(objectData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_ERROR,
                                RESPONSE_FAIL_ERROR);
                    }
                } else {
                    InputStream inputStream = objectData.getData();

                    if (StringUtils.isEmpty(cacheKey)) {
                        response.getLogger().warning(String.format("REDIS: Provide the cache key. Key value was: '%s'.",
                                cacheKey));
                        response.addEmptyResult(objectData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_ERROR, "Key is a required document property");
                        continue;
                    }

                    data = StreamUtil.toString(inputStream, StandardCharsets.UTF_8);

                    if (StringUtils.isNotEmpty(cacheExpiry)) {
                        response.getLogger().info("REDIS: Cache expiry has been specified. Creating key with expiry.");
                        result = redis.set(cacheKey, Integer.parseInt(cacheExpiry), data);
                    } else if (isHash) {
                        response.getLogger().info("REDIS: Cache field has been specified. Creating key with hash method.");
                        result = String.valueOf(redis.set(cacheKey, cacheField, data));
                    } else {
                        response.getLogger().info("REDIS: Cache key has been specified. Creating key normally.");
                        result = redis.set(cacheKey, data);
                    }
                    response.getLogger().fine(String.format("REDIS: 'SET %s' returned '%s'", cacheKey, result));

                    if (result.equals("OK")) {
                        OutputStream outputStream = getContext().createTempOutputStream();
                        StreamUtil.copy(inputStream, outputStream);
                        InputStream payloadStream = getContext().tempOutputStreamToInputStream(outputStream);
                        response.getLogger().fine(String.format("REDIS: 'SET %s' was successful. Response value " +
                                "was: '%s'.", cacheKey, result));
                        response.addResult(objectData, OperationStatus.SUCCESS, RESPONSE_SUCCESS, RESPONSE_SUCCESS,
                                PayloadUtil.toPayload(payloadStream));
                        return;
                    } else {
                        response.getLogger().warning(String.format("REDIS: 'SET %s' was not set. Response value was: '%s'.", cacheKey, result));
                        response.addEmptyResult(objectData, OperationStatus.APPLICATION_ERROR, RESPONSE_FAIL_ERROR, "Unexpected Error");
                    }
                }
            } catch (Exception e) {
                response.getLogger().severe(String.format("REDIS: An unexpected error has occurred: '%s'", e.getMessage()));
                response.addErrorResult(objectData, OperationStatus.FAILURE, RESPONSE_FAIL_ERROR, e.getMessage(), e);
            }
        }
    }
}


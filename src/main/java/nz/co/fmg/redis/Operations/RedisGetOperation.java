package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseGetOperation;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisRepository;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.Constants;
import nz.co.fmg.redis.Utils.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class RedisGetOperation extends BaseGetOperation {
    private final RedisRepository redis;

    public RedisGetOperation(RedisConnection conn) {
        super(conn);
        redis = conn.getRedisInstance();
    }

    @Override
    protected void executeGet(GetRequest request, OperationResponse response) {
        Logger logger = response.getLogger();
        ObjectIdData objectId = request.getObjectId();

        String cacheKey = BoomiUtils.GetPrefixedKey(objectId, "cacheKey");
        if (StringUtils.isEmpty(cacheKey)) {
            logger.severe("REDIS: GET operation failed because the cache key was empty.");
            response.addErrorResult(objectId, OperationStatus.APPLICATION_ERROR, Constants.HTTP_400, Constants.BAD_REQUEST, new Exception("The cache key is a required document property"));
        }

        String cacheField = BoomiUtils.GetDynamicProperty(objectId, "cacheInnerKey");
        String cacheJSONArrayOutput = BoomiUtils.GetDynamicProperty(objectId, "cacheJSONArrayOutput");

        boolean isArray = Objects.equals(cacheJSONArrayOutput, "true");
        boolean isHash = StringUtils.isNotEmpty(cacheField);
        boolean getAll = BoomiUtils.GetOperationBoolProperty(getContext(), "getAll");
        logger.info(String.format("REDIS: Retrieving key '%s' \n Field: '%s'", cacheKey, cacheField));
        try {
            String cacheResponse = null;
            Map<String, String> cacheValueMap;
            if (getAll) {
                cacheValueMap = redis.getAll(cacheKey);
                String jsonValues = StringUtils.reduceMap(cacheValueMap, isArray);
                response.addResult(objectId, OperationStatus.SUCCESS, "200", "OK", ResponseUtil.toPayload(jsonValues));
            } else if (isHash) {
                cacheResponse = redis.get(cacheKey, cacheField);
            } else {
                cacheResponse = redis.get(cacheKey);
            }
            logger.info(String.format("REDIS: 'GET '%s' returned '%s'", cacheKey, cacheResponse));

            if (cacheResponse != null) {
                response.addResult(objectId, OperationStatus.SUCCESS, Constants.HTTP_200, Constants.OK, ResponseUtil.toPayload(Constants.OK));
            } else {
                response.addEmptyResult(objectId, OperationStatus.APPLICATION_ERROR, Constants.HTTP_404, Constants.NOT_FOUND);
            }

        } catch (Exception e) {
            ResponseUtil.addExceptionFailure(response, objectId, e);
        }
    }
}

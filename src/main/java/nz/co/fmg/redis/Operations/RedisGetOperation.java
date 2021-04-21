package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseGetOperation;
import nz.co.fmg.redis.Repository.RedisCommands;
import nz.co.fmg.redis.Logging.ContainerLogger;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Utils.BoomiUtils;
import nz.co.fmg.redis.Utils.StringUtils;

import java.util.Map;
import java.util.logging.Logger;

import static com.boomi.connector.api.ResponseUtil.toPayload;

public class RedisGetOperation extends BaseGetOperation {
    private final RedisCommands cache;

    public RedisGetOperation(RedisConnection conn) {
        super(conn);
        cache = conn.getRedisInstance();
    }

    @Override
    protected void executeGet(GetRequest request, OperationResponse response) {
        Logger logger = ContainerLogger.getResponseLogger(response);
        ObjectIdData objectId = request.getObjectId();

        String cacheKey = BoomiUtils.GetPrefixedKey(objectId, "cacheKey");
        String cacheInnerKey = BoomiUtils.GetDynamicProperty(objectId, "cacheInnerKey");
        String cacheJSONArrayOutput = BoomiUtils.GetDynamicProperty(objectId, "cacheJSONArrayOutput");

        boolean isArray = cacheJSONArrayOutput.equals("true");
        boolean isHash = StringUtils.isNotEmpty(cacheInnerKey);
        boolean getAll = BoomiUtils.GetOperationBoolProperty(getContext(), "getAll");

        String cacheResponse = null;
        Map<String, String> cacheValueMap;

        try {
            if (StringUtils.isEmpty(cacheKey)) {
                logger.warning("The cache key was either null or empty.");
                throw new Exception("Cache Key cannot be null.");
            }

            if (getAll) {
                cacheValueMap = cache.getAll(cacheKey);
                String jsonValues = StringUtils.reduceMap(cacheValueMap, isArray);
                response.addResult(objectId, OperationStatus.SUCCESS, "200", "OK", toPayload(jsonValues));
            } else if (isHash) {
                cacheResponse = cache.get(cacheKey, cacheInnerKey);
            } else {
                cacheResponse = cache.get(cacheKey);
            }

            if (cacheResponse != null) {
                response.addResult(objectId, OperationStatus.SUCCESS, "200", "OK", toPayload(cacheResponse));
            }

        } catch (Exception e) {
            ResponseUtil.addExceptionFailure(response, objectId, e);
        }
    }
}

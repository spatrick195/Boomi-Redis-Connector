package nz.co.fmg;

import nz.co.fmg.redis.Utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class TestUtilities {

    public static Map<String, Object> getConnectionProperties() {
        Map<String, Object> connectionProperties = new HashMap<>();
        connectionProperties.put("redisHostURL", Constants.HOST_PROPERTY);
        connectionProperties.put("redisHostPort", Constants.PORT_PROPERTY);
        connectionProperties.put("redisHostPassword", Constants.PASSWORD_PROPERTY);
        connectionProperties.put("redisConnectionTimeout", Constants.CONN_TIMEOUT_PROPERTY);
        connectionProperties.put("redisCluster", Constants.CLUSTER_ENABLED_PROPERTY);
        connectionProperties.put("redisEnableExpiry", Constants.EXPIRY_ENABLED_PROPERTY);
        connectionProperties.put("redisExpiryTime", Constants.EXPIRY_TIME_PROPERTY);
        return connectionProperties;
    }

    public static Map<String, Object> getGetOperationProperties() {
        Map<String, Object> operationProperties = new HashMap<>();
        operationProperties.put("getAll", "false");
        return operationProperties;
    }
}

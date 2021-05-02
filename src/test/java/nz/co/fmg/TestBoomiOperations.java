package nz.co.fmg;

import com.boomi.connector.api.ObjectDefinitionRole;
import com.boomi.connector.api.OperationType;
import com.boomi.connector.testutil.ConnectorTester;
import nz.co.fmg.redis.RedisConnector;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestBoomiOperations {
    private final ConnectorTester connectorTester;


    public TestBoomiOperations() {
        RedisConnector connector = new RedisConnector();
        connectorTester = new ConnectorTester(connector);
    }
    public void testGetOperations() {
        Map<String, Object> connProps = getConnectionProperties();
        Map<String, Object> opProps = getGetOperationProperties();
        Map<ObjectDefinitionRole, String> cookies = new HashMap<>();
        cookies.put(ObjectDefinitionRole.INPUT, "cacheKey");
        connectorTester.setOperationContext(OperationType.GET, connProps, opProps, null, cookies);
        connectorTester.executeGetOperation("OUTPUT");
    }

    private Map<String, Object> getConnectionProperties() {
        Map<String, Object> connectionProperties = new HashMap<>();
        connectionProperties.put("redisHostURL", "127.0.0.1");
        connectionProperties.put("redisHostPort", "6379");
        connectionProperties.put("redisHostPassword", "saxophone");
        connectionProperties.put("redisConnectionTimeout", "2000");
        connectionProperties.put("redisCluster", "false");
        connectionProperties.put("redisEnableExpiry", "false");
        connectionProperties.put("redisExpiryTime", "0");
        return connectionProperties;
    }

    private Map<String, Object> getGetOperationProperties() {
        Map<String, Object> operationProperties = new HashMap<>();
        operationProperties.put("getAll", "false");
        operationProperties.put("CacheKey", "k");
        operationProperties.put("cacheKey", "kk");
        operationProperties.put("cacheValue", "kk");
        return operationProperties;
    }
}

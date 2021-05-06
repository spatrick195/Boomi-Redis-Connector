package nz.co.fmg;

import com.boomi.connector.api.ObjectDefinitionRole;
import com.boomi.connector.api.OperationType;
import com.boomi.connector.testutil.ConnectorTester;
import nz.co.fmg.redis.RedisConnector;

import java.util.HashMap;
import java.util.Map;

public class TestBoomiOperations {
    private final ConnectorTester connectorTester;


    public TestBoomiOperations() {
        RedisConnector connector = new RedisConnector();
        connectorTester = new ConnectorTester(connector);
    }

    public void testGetOperations() {
        Map<String, Object> connProps = TestUtilities.getConnectionProperties();
        Map<String, Object> opProps = TestUtilities.getGetOperationProperties();
        Map<ObjectDefinitionRole, String> cookies = new HashMap<>();
        cookies.put(ObjectDefinitionRole.INPUT, "cacheKey");
        connectorTester.setOperationContext(OperationType.GET, connProps, opProps, null, cookies);
        connectorTester.executeGetOperation("OUTPUT");
    }

}

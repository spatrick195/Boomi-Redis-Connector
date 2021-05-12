package nz.co.fmg;

import com.boomi.connector.api.OperationType;
import com.boomi.connector.testutil.*;
import nz.co.fmg.redis.RedisConnector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nz.co.fmg.TestUtilities.getConnectionProperties;
import static nz.co.fmg.TestUtilities.getGetOperationProperties;


public class TestBoomiBrowse {
    private final ConnectorTester tester;
    private final SimpleBrowseContext browseContext;
    private final List<SimpleTrackedData> simpleData;

    SimpleOperationContext context;
    List<SimpleOperationResult> expectedResults;
    SimplePayloadMetadata metadata;

    public TestBoomiBrowse() {
        RedisConnector connection = new RedisConnector();
        SimpleAtomConfig atomConfig = new SimpleAtomConfig();
        tester = new ConnectorTester(connection);
        browseContext = new SimpleBrowseContext(atomConfig, connection, OperationType.CREATE, getConnectionProperties(), getGetOperationProperties());
        context = new SimpleOperationContext(atomConfig, connection, OperationType.CREATE, getConnectionProperties(), getGetOperationProperties(), "CREATE", null);
        tester.setOperationContext(context);
        simpleData = new ArrayList<>();
        metadata = new SimplePayloadMetadata();
    }

    @Test
    public void testBadBrowse() {
        Map<String, String> dynamicProperties = new HashMap<>();
        dynamicProperties.put("CacheKey", "TestInstanceOfKey");
        dynamicProperties.put("CacheValue", "TestInstanceOfValue");
        SimpleTrackedData trackedData = new SimpleTrackedData(0, "TEST", null, dynamicProperties, null);
        tester.getConnector().createBrowser(browseContext);
        simpleData.add(trackedData);

        expectedResults = new ArrayList<>();
        SimpleOperationResult result = new SimpleOperationResult();
        SimpleOperationResponse response = new SimpleOperationResponse();
        response.createMetadata();
        expectedResults.add(result);
        metadata.setTrackedProperty("CacheKey", "Value");
        metadata.setUserDefinedProperty("CacheValue", "value");
        tester.testExecuteCreateOperationWithTrackedData(simpleData, expectedResults);
    }
}

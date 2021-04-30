package nz.co.fmg;

import com.boomi.connector.api.PropertyMap;
import com.boomi.connector.testutil.ConnectorTester;
import nz.co.fmg.redis.RedisConnector;

public class TestGetOperation {
    private final RedisConnector connector;
    private final ConnectorTester connectorTester;
    private TestGetOperation(){
        connector = new RedisConnector();
        connectorTester = new ConnectorTester();
        PropertyMap propertyMap = connectorTester.getBrowseContext().getConnectionProperties();
        propertyMap.put("cacheKey", "ping");

    }
}

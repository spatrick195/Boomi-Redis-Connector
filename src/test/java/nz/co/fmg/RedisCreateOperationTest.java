package nz.co.fmg;

import com.boomi.connector.api.OperationType;
import com.boomi.connector.testutil.ConnectorTester;
import org.junit.Test;

import java.util.HashMap;

public class RedisCreateOperationTest {
    private static void setOperationContext(ConnectorTester tester, OperationType opType,
                                            HashMap<String, Object> connProps, HashMap<String, Object> opProps,
                                            String objectType) {
        tester.setOperationContext(opType, connProps, opProps, objectType, null);
    }

    @Test
    public void testCreateOperation() {
//        RedisConnector connector = new RedisConnector();
//        ConnectorTester tester = new ConnectorTester(connector);
//        HashMap<String, Object> connProps = new HashMap<>();
//        HashMap<String, Object> opProps = new HashMap<>();
//        connProps.put("redisHostPort", 6739);
//        connProps.put("redisHostURL", "abc.com");
//        connProps.put("redisHostPassword", "abc");
//        connProps.put("redisConnectionTimeout", 5);
//        connProps.put("redisClusterEnabled", false);
//        connProps.put("redisEnableExpiry", false);
//        connProps.put("redisTimeToExpire", 600);
//
//        opProps.put("cacheKey", "xyz");
//        opProps.put("cacheInnerKey", "zyx");
//        opProps.put("cacheValue", "lorem ipsum");
//
//        ExecutionUtil.setDynamicProcessProperty("cacheKey", "xyz", false);
//
//        setOperationContext(tester, OperationType.CREATE, connProps, opProps, "CREATE");

    }
}

package nz.co.fmg.redis;

import com.boomi.connector.api.BrowseContext;
import com.boomi.connector.api.Browser;
import com.boomi.connector.api.Operation;
import com.boomi.connector.api.OperationContext;
import com.boomi.connector.util.BaseConnector;
import nz.co.fmg.redis.Operations.RedisCreateOperation;
import nz.co.fmg.redis.Operations.RedisDeleteOperation;
import nz.co.fmg.redis.Operations.RedisGetOperation;

public class RedisConnector extends BaseConnector {

    @Override
    public Browser createBrowser(BrowseContext context) {
        return new RedisBrowser(createConnection(context));
    }

    @Override
    protected Operation createGetOperation(OperationContext context) {
        return new RedisGetOperation(createConnection(context));
    }

    @Override
    protected Operation createCreateOperation(OperationContext context) {
        return new RedisCreateOperation(createConnection(context));
    }

    @Override
    protected Operation createDeleteOperation(OperationContext context) {
        return new RedisDeleteOperation(createConnection(context));
    }

    private RedisConnection createConnection(BrowseContext context) {
        return new RedisConnection(context);
    }
}

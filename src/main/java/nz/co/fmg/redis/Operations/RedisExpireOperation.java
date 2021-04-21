package nz.co.fmg.redis.Operations;

import com.boomi.connector.api.OperationResponse;
import com.boomi.connector.api.QueryRequest;
import com.boomi.connector.util.BaseQueryOperation;
import nz.co.fmg.redis.RedisConnection;
import nz.co.fmg.redis.Repository.RedisCommands;

public class RedisExpireOperation extends BaseQueryOperation {
    protected RedisExpireOperation(RedisConnection conn) {
        super(conn);
        RedisCommands redis = conn.getRedisInstance();
    }

    @Override
    protected void executeQuery(QueryRequest request, OperationResponse response) {

    }
}

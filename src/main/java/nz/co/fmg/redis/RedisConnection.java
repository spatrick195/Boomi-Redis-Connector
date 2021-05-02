package nz.co.fmg.redis;

import com.boomi.connector.api.BrowseContext;
import com.boomi.connector.api.PropertyMap;
import com.boomi.connector.util.BaseConnection;
import nz.co.fmg.redis.Repository.RedisCommands;
import nz.co.fmg.redis.Repository.RedisRepository;

public class RedisConnection extends BaseConnection<BrowseContext> {

    private final String _host;
    private final Long _port;
    private final String _password;
    private final Long _timeOut;
    private final Boolean _isCluster;
    private final Boolean _expiryEnabled;
    private final Long _expiryTime;
    private RedisRepository redis;

    public RedisConnection(BrowseContext context) {
        super(context);
        PropertyMap propertyMap = context.getConnectionProperties();
        _host = String.valueOf(propertyMap.getProperty("redisHostURL"));
        _port = Long.parseLong(propertyMap.getProperty("redisHostPort"));
        _password = String.valueOf(propertyMap.getProperty("redisHostPassword"));
        _timeOut = Long.parseLong(propertyMap.getProperty("redisConnectionTimeout"));
        _isCluster = Boolean.getBoolean(propertyMap.getProperty("redisCluster"));
        _expiryEnabled = Boolean.getBoolean(propertyMap.getProperty("redisEnableExpiry"));
        _expiryTime = Long.parseLong(propertyMap.getProperty("redisExpiryTime"));
    }

    public RedisRepository getRedisInstance() {
        if (redis == null) {
            redis = new RedisCommands(_host, _port, _password, _timeOut, _isCluster, _expiryEnabled, _expiryTime);
        }
        return redis;
    }
}

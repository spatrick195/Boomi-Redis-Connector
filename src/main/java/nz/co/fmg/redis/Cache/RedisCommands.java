package nz.co.fmg.redis.Cache;

import nz.co.fmg.redis.Logging.ContainerLogger;
import nz.co.fmg.redis.Utils.ScanIterator;
import nz.co.fmg.redis.Utils.StringUtils;
import redis.clients.jedis.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static nz.co.fmg.redis.Utils.ErrorUtils.throwException;

public class RedisCommands extends BaseRedisCommands {
    private final String _host;
    private final Long _port;
    private final String _password;
    private final Long _connTimeout;
    private final Boolean _clusterEnabled;
    private final Boolean _expiryEnabled;
    private final Long _expiryTime;
    JedisPool pool;
    JedisPoolConfig poolConfig;
    JedisClusterConnectionHandler jedisConnHandler;

    /**
     * @param host           A string value that contains the host of the redis server defined in the connection component
     * @param port           A long value that contains the port of the redis host in the connection component
     * @param password       A string value that contains the password to the redis server
     * @param connTimeout    The long value of the connection timeout
     * @param clusterEnabled A boolean value whether cluster is true/false in the connection component
     */
    public RedisCommands(String host, Long port, String password, Long connTimeout, Boolean clusterEnabled,
                         Boolean expiryEnabled, Long expiryTime) {
        super();
        this._host = host;
        this._port = port;
        this._password = password;
        this._connTimeout = connTimeout;
        this._clusterEnabled = clusterEnabled;
        this._expiryEnabled = expiryEnabled;
        this._expiryTime = expiryTime;
        InitializeConnection();
    }

    /**
     * Initialize the connection to Redis based on the values in the connector
     */
    public void InitializeConnection() {
        if (StringUtils.isEmpty(_host)) {
            throwException(new Exception("The value for host was empty. Check if you have a host to connect to."));
        } else {
            poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(200);
            poolConfig.setMaxWaitMillis(7000);
            int socketTimeout = 7000;
            int connectionTimeout = 7000;
            int maxAttempts = 5;
            if (_clusterEnabled) {
                ContainerLogger.getLogger().info("Attempting to create redis cluster connection... " + _host);

                JedisCluster cluster;
                HostAndPort clusterNode = new HostAndPort(_host, _port.intValue());

                if (StringUtils.isEmpty(_password)) {
                    cluster = new JedisCluster(clusterNode, connectionTimeout, socketTimeout, poolConfig);
                } else {
                    cluster = new JedisCluster(clusterNode, connectionTimeout, socketTimeout, maxAttempts, _password, poolConfig);
                }
                Field connectionHandlerField;
                try {
                    // gets JedisClusterConnectionHandler from the JedisBinaryCluster class which is extended by
                    // JedisCluster;
                    connectionHandlerField = JedisCluster.class.getDeclaredField("connectionHandler");
                    // suppress checks for Java language access control
                    // use the JedisClusterConnectionHandler we retrieved earlier to get the cluster
                    jedisConnHandler = (JedisClusterConnectionHandler) connectionHandlerField.get(cluster);
                } catch (Exception e) {
                    throwException(new Exception(e));
                }

                ContainerLogger.getLogger().info("Connection to redis cluster was successful.");
            } else {
                ContainerLogger.getLogger().info("Attempting to create connection pool...");

                if (StringUtils.isEmpty(_password)) {
                    pool = new JedisPool(poolConfig, _host, _port.intValue(), _connTimeout.intValue());
                } else {
                    pool = new JedisPool(poolConfig, _host, _port.intValue(), _connTimeout.intValue(),
                            _password);
                }
                ContainerLogger.getLogger().info("Connection to redis pool was successful.");
            }
        }
    }

    /**
     * @return Returns an instance of Jedis based on if the connection is a cluster or pool.
     */
    public Jedis getJedis() {
        if (jedisConnHandler != null) {
            return ((JedisSlotBasedConnectionHandler) jedisConnHandler).getConnection();
        } else {
            return pool.getResource();
        }
    }

    /**
     * @param key The redis key to be queried
     * @return Returns all the fields and associated values in a hash.
     */
    public Map<String, String> hGetAll(String key) {
        Map<String, String> result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hgetAll(key);
        } finally {
            releaseJedis(jedis);
        }

        return result;
    }

    /**
     * @param key   The Redis key to be queried
     * @param field The Redis field to be queried
     * @return Returns the value associated with the specified field, if it is not found the method will return nil
     */
    public String hGet(String key, String field) {
        String result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hget(key, field);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    /**
     * @param key The Redis key to be queried
     * @return Return the value of specified key, if it does not exist it will return NULL. If the key is not of type
     * string, it will return a string error.
     */
    @Override
    public String get(String key) {
        String result;
        Jedis jedis = getJedis();
        try {
            result = jedis.get(key);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    /**
     * @param key   The name of the key
     * @param time  The expiry time
     * @param value The value of the key
     * @return Returns OK if successful
     */
    @Override
    public String setEx(String key, int time, String value) {
        String result;
        Jedis jedis = getJedis();
        try {
            result = jedis.setex(key, time, value);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    /**
     * @param key   The name of the redis key to be stored
     * @param field The field of the value to be stored.
     * @param value The value to be stored
     * @return If a new field is created, the returned value will be 1, otherwise it will be 0.
     */public Long hSet(String key, String field, String value) {
        Long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hset(key, field, value);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    /**
     * @param key   The name of the key to be stored as
     * @param value The value of the key
     * @return Returns an HTTP status code response from the operation
     */
    @Override
    public String set(String key, String value) {
        String result;
        Jedis jedis = getJedis();
        try {
            result = jedis.set(key, value);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public long deletePattern(String pattern) {
        long result = 0L;
        Jedis jedis = getJedis();
        ScanParams params = new ScanParams().match(pattern);
        ScanIterator iterator = new ScanIterator(jedis, params);

        try {
            while (iterator.hasNext()) {
                List<String> keys = iterator.next();
                for (String key : keys) {
                    result = jedis.del(key);
                }
            }
        } finally {
            releaseJedis(jedis);
        }

        return result;
    }

    protected long deleteHPattern(String key, String pattern) {
        long result = 0;
        Jedis jedis = getJedis();
        ScanParams params = new ScanParams().match(pattern);
        ScanResult<Map.Entry<String, String>> results;
//
//        HScanIterator iterator = new HScanIterator(pattern, jedis, params);
//
//        try {
//            while (iterator.hasNext()) {
//                List<Map.Entry<String, String>> keys = iterator.next();
//            }
//        } finally {
//            releaseJedis(jedis);
//        }
        return result;
    }


    /**
     * @param cacheKey The key to be deleted
     * @return Returns 0 if no keys are deleted or 1 if keys are deleted
     */
    @Override
    public Long del(String cacheKey) {
        long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.del(cacheKey);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    /**
     * @param key The key to be deleted
     * @return Returns 0 if no keys are deleted or 1 if keys are deleted.
     */
    public Long hDel(String key, String field) {
        long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hdel(key, field);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    /**
     * @return Returns the value from the 'expiry enabled' option in the connection component from Boomi
     */
    public boolean UseExpiry() {
        if (_expiryEnabled != null) {
            return _expiryEnabled;
        } else {
            return false;
        }
    }

    /**
     * @return Returns the value from the 'expiry TTL' option in the connection component from Boomi
     */
    public int getExpiryTime() {
        if (_expiryTime != null) {
            return _expiryTime.intValue();
        } else {
            return 0;
        }
    }

    /**
     * @param jedis Specifies the jedis instance to be released
     */
    public void releaseJedis(Jedis jedis) {
        jedis.disconnect();
        jedis.close();
    }
}

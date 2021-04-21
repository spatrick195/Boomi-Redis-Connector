package nz.co.fmg.redis.Repository;

import nz.co.fmg.redis.Utils.ScanIterator;
import nz.co.fmg.redis.Utils.StringUtils;
import redis.clients.jedis.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static nz.co.fmg.redis.Utils.ErrorUtils.throwException;

public class RedisCommands extends RedisRepository {
    private final String _host;
    private final Long _port;
    private final String _password;
    private final Long _connTimeout;
    private final Boolean _clusterEnabled;
    private final Boolean _expiryEnabled;

    JedisPool pool;
    JedisPoolConfig poolConfig;
    JedisClusterConnectionHandler jedisConnHandler;

    public RedisCommands(final String host, final Long port, final String password, final Long timeout,
                         final Boolean clusterEnabled,
                         final Boolean expiryEnabled) {
        super(host, port, password, timeout, clusterEnabled, expiryEnabled);
        _host = host;
        _port = port;
        _password = password;
        _connTimeout = timeout;
        _clusterEnabled = clusterEnabled;
        _expiryEnabled = expiryEnabled;
        InitializeConnection(host, port, password, timeout, clusterEnabled, expiryEnabled);
    }

    @Override
    protected void InitializeConnection(final String host, final Long port, final String password, final Long timeout,
                                        final Boolean clusterEnabled, final Boolean expiryEnabled) {
        if (StringUtils.isEmpty(_host)) {
            throwException(new Exception("The value for host was empty."));
        }
        poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxWaitMillis(Integer.parseInt(String.valueOf(_connTimeout)));
        int socketTimeout = Integer.parseInt(String.valueOf(_connTimeout));
        int connectionTimeout = Integer.parseInt(String.valueOf(_connTimeout));
        int maxAttempts = 5;
        if (_clusterEnabled) {
            try {
                JedisCluster cluster;
                // the host and port
                HostAndPort clusterNode = new HostAndPort(_host, _port.intValue());
                // create the cluster connection
                cluster = new JedisCluster(clusterNode, connectionTimeout, socketTimeout, maxAttempts, _password, poolConfig);
                // gets ConnectionHandler from the JedisBinaryCluster class which is extended by JedisCluster;
                Field connectionHandlerField = JedisCluster.class.getDeclaredField("connectionHandler");
                // get the cluster info
                jedisConnHandler = (JedisClusterConnectionHandler) connectionHandlerField.get(cluster);
            } catch (Exception e) {
                throwException(new Exception(e));
            }
        } else {
            pool = new JedisPool(poolConfig, _host, _port.intValue(), _connTimeout.intValue(), _password);
        }
    }

    @Override
    public Map<String, String> getAll(String key) {
        Map<String, String> result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hgetAll(key);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

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

    @Override
    public String get(String key, String field) {
        String result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hget(key, field);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

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
    public Long set(String key, String field, String value) {
        Long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hset(key, field, value);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public String set(String key, int expiry, String value) {
        String result;
        Jedis jedis = getJedis();
        try {
            result = jedis.setex(key, expiry, value);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public Long pattern(String pattern) {
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

    @Override
    public Long delete(String key) {
        long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.del(key);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public Long delete(String key, String field) {
        long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.hdel(key, field);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public Long expire(String key) {
        long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.expire(key, 0);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public Long expire(String key, Integer seconds) {
        long result;
        Jedis jedis = getJedis();
        try {
            result = jedis.expire(key, seconds);
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public Long hashPattern(String pattern) {
        Jedis jedis = getJedis();
        HashSet<String> keys = new HashSet<>();
        try {
            keys.addAll(jedis.hkeys(pattern));
        } finally {
            releaseJedis(jedis);
        }
        return (long) keys.size();
    }

    @Override
    protected Jedis getJedis() {
        if (jedisConnHandler != null) {
            return ((JedisSlotBasedConnectionHandler) jedisConnHandler).getConnection();
        } else {
            return pool.getResource();
        }
    }

    @Override
    protected void releaseJedis(Jedis jedis) {
        jedis.disconnect();
        jedis.close();
    }
}

package nz.co.fmg.redis.Repository;

import nz.co.fmg.redis.Utils.StringUtils;
import redis.clients.jedis.*;

import java.lang.reflect.Field;
import java.util.Map;

import static nz.co.fmg.redis.Utils.ErrorUtils.throwException;

public abstract class RedisRepository implements IRepository {
    private JedisPool pool;
    private JedisClusterConnectionHandler jedisConnHandler;

    protected RedisRepository() {
    }

    protected void InitializeConnection(final String host, final Long port, final String password, final Long timeout,
                                        final Boolean clusterEnabled) {
        if (StringUtils.isEmpty(host)) {
            throwException(new Exception("The value for host was empty."));
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxWaitMillis(timeout.intValue());
        int socketTimeout = timeout.intValue();
        int connectionTimeout = timeout.intValue();
        int maxAttempts = 5;

        if (clusterEnabled) {
            try {
                JedisCluster cluster;
                // the host and port
                String connectionHandler = "connectionHandler";
                HostAndPort clusterNode = new HostAndPort(host, port.intValue());
                // create the cluster connection
                cluster = new JedisCluster(clusterNode, connectionTimeout, socketTimeout, maxAttempts, password, poolConfig);
                // gets ConnectionHandler from the JedisBinaryCluster class which is extended by JedisCluster;
                Field connectionHandlerField = JedisCluster.class.getDeclaredField(connectionHandler);
                // get the cluster info
                jedisConnHandler = (JedisClusterConnectionHandler) connectionHandlerField.get(cluster);

            } catch (Exception e) {
                throwException(new Exception(e));
            }
        } else {
            pool = new JedisPool(poolConfig, host, port.intValue(), timeout.intValue(), password);
        }
    }

    public abstract Map<String, String> getAll(String key);

    public abstract String get(String key);
    public abstract String get(String key, String field);
    public abstract String set(String key, String value);
    public abstract String set(String key, int expiry, String value);
    public abstract Long set(String key, String field, String value);
    public abstract Long delete(String key);
    public abstract Long delete(String key, String value);
    public abstract Long pattern(String key);
    public abstract Long hashPattern(String key);
    public abstract Long expire(String key);
    public abstract Long expire(String key, Integer seconds);
    public abstract Long getExpiry();
    public abstract Boolean isExpiry();
    protected Jedis getJedis() {
        if (jedisConnHandler != null) {
            return ((JedisSlotBasedConnectionHandler) jedisConnHandler).getConnection();
        } else {
            return pool.getResource();
        }
    }

    protected void releaseJedis(Jedis jedis) {
        jedis.disconnect();
        jedis.close();
    }
}

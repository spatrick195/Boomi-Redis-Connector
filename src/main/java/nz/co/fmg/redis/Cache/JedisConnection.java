package nz.co.fmg.redis.Cache;

import nz.co.fmg.redis.Logging.ContainerLogger;
import nz.co.fmg.redis.Utils.StringUtils;
import redis.clients.jedis.*;

import java.lang.reflect.Field;

import static nz.co.fmg.redis.Utils.ErrorUtils.throwException;

class JedisConnection extends BaseRedisConnection{
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
    public JedisConnection(String host, Long port, String password, Long connTimeout, Boolean clusterEnabled,
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


    @Override
    void InitializeConnection() {
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

    @Override
    Jedis getJedis() {
        if (jedisConnHandler != null) {
            return ((JedisSlotBasedConnectionHandler) jedisConnHandler).getConnection();
        } else {
            return pool.getResource();
        }
    }

    @Override
    void releaseJedis(Jedis jedis) {
        jedis.disconnect();
        jedis.close();
    }
}

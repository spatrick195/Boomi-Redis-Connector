package nz.co.fmg.redis.Repository;

import redis.clients.jedis.Jedis;

public abstract class RedisRepository implements IRepository {

    protected RedisRepository(final String host, final Long port, final String password, final Long timeout,
                                final Boolean clusterEnabled, final Boolean expiryEnabled) {
        InitializeConnection(host, port, password, timeout, clusterEnabled, expiryEnabled);
    }

    protected abstract void InitializeConnection(final String host, final Long port, final String password, final Long timeout,
                                                 final Boolean clusterEnabled, final Boolean expiryEnabled);

    protected abstract Jedis getJedis();

    protected abstract void releaseJedis(Jedis jedis);
}

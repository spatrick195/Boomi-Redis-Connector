package nz.co.fmg.redis.Cache;

import redis.clients.jedis.Jedis;

abstract class BaseRedisConnection {
    BaseRedisConnection() {
    }

    /**
     * Initialize the connection to Redis based on the values in the connector
     */
    abstract void InitializeConnection();

    /**
     * @return Returns an instance of Jedis based on if the connection is a cluster or pool.
     */
    abstract Jedis getJedis();

    /**
     * @param jedis Specifies the jedis instance to be released
     */
    abstract void releaseJedis(Jedis jedis);
}

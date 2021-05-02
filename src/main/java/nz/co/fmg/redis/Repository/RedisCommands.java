package nz.co.fmg.redis.Repository;

import nz.co.fmg.redis.Utils.ScanIterator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;

import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class RedisCommands extends RedisRepository {
    private final Long _expiryTimeout;
    private final Boolean _expiryEnabled;

    public RedisCommands(String host, Long port, String password, Long timeout,
                         Boolean clusterEnabled, Boolean expiryEnabled, Long expiryTime) {
        super.InitializeConnection(host, port, password, timeout, clusterEnabled);
        _expiryTimeout = expiryTime;
        _expiryEnabled = expiryEnabled;
    }

    @Override
    public Map<String, String> getAll(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hgetAll(key);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public String get(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public String get(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hget(key, field);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public String set(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Long set(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hset(key, field, value);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public String set(String key, int expiry, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setex(key, expiry, value);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Long pattern(String pattern) {
        Long result = null;
        Jedis jedis = getJedis();
        ScanParams params = new ScanParams().match(pattern);
        ScanIterator iterator = new ScanIterator(jedis, params);
        try {
            while (iterator.hasNext()) {
                List<String> keys = iterator.next();
                result = (long) keys.size();
                for (String key : keys) {
                    jedis.del(key);
                }
            }
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }

    @Override
    public Long delete(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(key);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Long delete(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(key, field);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Long expire(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.expire(key, 0);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Long expire(String key, Integer seconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.expire(key, seconds);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Boolean isExpiry() {
        return _expiryEnabled;
    }

    @Override
    public Long getExpiry() {
        return _expiryTimeout;
    }

    @Override
    public Long hashPattern(String pattern) {
        Jedis jedis = getJedis();
        HashSet<String> keys;
        try {
            keys = new HashSet<>(jedis.hkeys(pattern));
            for (String key : keys) {
                jedis.del(key);
            }
        } finally {
            releaseJedis(jedis);
        }
        return (long) keys.size();
    }
}

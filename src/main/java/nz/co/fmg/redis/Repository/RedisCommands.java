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

    public RedisCommands(final String host, final Long port, final String password, final Long timeout,
                         final Boolean clusterEnabled,
                         final Boolean expiryEnabled, final Long expiryTime) {
        InitializeConnection(host, port, password, timeout, clusterEnabled);
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
        Long result = null;
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

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
        long result;
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
        Jedis jedis = getJedis();
        ScanParams params = new ScanParams().match(pattern);
        ScanIterator iterator = new ScanIterator(jedis, params);
        long result = 0;
        try {
            while (iterator.hasNext()) {
                List<String> keys = iterator.next();
                result = keys.size();
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
    public String ping(){
        String result;
        Jedis jedis = getJedis();
        try{
            result = jedis.ping();
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
    public void expire(String key, Integer seconds) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, seconds);
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
    public Long keys(String pattern) {
        long result;
        HashSet<String> keys;
        Jedis jedis = getJedis();
        try {
            keys = new HashSet<>(jedis.hkeys(pattern));
            result = keys.size();
            for (String key : keys) {
                jedis.del(key);
            }
        } finally {
            releaseJedis(jedis);
        }
        return result;
    }
}

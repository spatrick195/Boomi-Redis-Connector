package nz.co.fmg.redis.Cache;

import java.util.Map;

public class RedisHashCommands extends BaseRedisHashCommands {
    @Override
    public Map<String, String> hGetAll(String key) {
        return null;
    }

    @Override
    public String hGet(String key, String field) {
        return null;
    }

    @Override
    public Long hSet(String key, String field, String value) {
        return null;
    }

    @Override
    public Long hPattern(String key, String pattern) {
        return 0L;
    }

    @Override
    public  Long hDel(String key, String field) {
        return null;
    }
}

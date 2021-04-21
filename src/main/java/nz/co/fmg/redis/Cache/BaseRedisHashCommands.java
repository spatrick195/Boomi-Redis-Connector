package nz.co.fmg.redis.Cache;

import java.util.Map;

public abstract class BaseRedisHashCommands implements IBaseRedisHashCommands{
    /**
     * @param key The redis key to be queried
     * @return Returns all the fields and associated values in a hash.
     */
    public abstract Map<String, String> hGetAll(String key);

    /**
     * @param key   The Redis key to be queried
     * @param field The Redis field to be queried
     * @return Returns the value associated with the specified field, if it is not found the method will return nil
     */
    public abstract String hGet(String key, String field);

    /**
     * @param key   The name of the redis key to be stored
     * @param field The field of the value to be stored.
     * @param value The value to be stored
     * @return If a new field is created, the returned value will be 1, otherwise it will be 0.
     */
    public abstract Long hSet(String key, String field, String value);

    /**
     * @param key     The key to search in
     * @param pattern The pattern to match
     * @return Returns an HTTP response e.g. 'OK'
     */
    public abstract Long hPattern(String key, String pattern);

    /**
     * @param key The key to be deleted
     * @return Returns 0 if no keys are deleted or 1 if keys are deleted.
     */
    public abstract Long hDel(String key, String field);
}
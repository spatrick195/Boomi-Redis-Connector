package nz.co.fmg.redis.Cache;

public abstract class BaseRedisCommands {

    /**
     * @param key The Redis key to be queried
     * @return Return the value of specified key, if it does not exist it will return NULL. If the key is not of type
     * string, it will return a string error.
     */
    abstract String get(String key);

    /**
     * @param key   The name of the key to be stored as
     * @param value The value of the key
     * @return Returns an HTTP status code response from the operation
     */
    abstract String set(String key, String value);

    /**
     * @param pattern The pattern to match
     * @return Returns
     */
    abstract long deletePattern(String pattern);

    /**
     * @param cacheKey The key to be deleted
     * @return Returns 0 if no keys are deleted or 1 if keys are deleted
     */
    abstract Long del(String cacheKey);

    /**
     * @param key   The key to be created
     * @param time  The time to live
     * @param value The value of the data to be stored
     * @return Returns an HTTP response e.g. 'OK'
     */
    abstract String setEx(String key, int time, String value);

}

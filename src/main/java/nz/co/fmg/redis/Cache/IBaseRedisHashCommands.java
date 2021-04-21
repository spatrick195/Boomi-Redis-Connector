package nz.co.fmg.redis.Cache;

import java.util.Map;

public interface IBaseRedisHashCommands {
    /**
     * @param key The key to be retrieved
     * @return Returns all fields within the key.
     */
    Map<String, String> hGetAll(String key);

    /**
     * @param key   The key to be retrieved
     * @param field The inner-key/field to be retrieved
     * @return Returns the value of the key
     */
    String hGet(String key, String field);

    /**
     * @param key   The name of the key to be created
     * @param field The name of the field to be created
     * @param value The value to be created
     * @return Returns an HTTP response e.g. 'OK'
     */
    Long hSet(String key, String field, String value);

    /**
     * @param key     The name of the key to be queried
     * @param pattern The pattern to be matched
     * @return Returns all fields which match the pattern
     */
    Long hPattern(String key, String pattern);

    /**
     * @param key   The name of the key to be queried
     * @param field The field to be deleted
     * @return Returns an HTTP Response e.g. 'OK'
     */
    Long hDel(String key, String field);
}

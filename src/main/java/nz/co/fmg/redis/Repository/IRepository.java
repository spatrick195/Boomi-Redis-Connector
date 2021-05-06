package nz.co.fmg.redis.Repository;

import java.util.Map;

public interface IRepository {
    /**
     * Returns all fields and values of the hash stored at key.
     * In the returned value, every field name is followed by its value,
     * so the length of the reply is twice the size of the hash.
     *
     * @param key The key to be retrieved
     * @return Returns all fields and values of the hash stored at key. In the returned value, every field name is followed by its value, so the length of the reply is twice the size of the hash.
     */
    Map<String, String> getAll(String key);

    /**
     * Get the value of key. If the key does not exist the special value nil is returned.
     * An error is returned if the value stored at key is not a string, because GET only handles string values.
     *
     * @param key The key to be retrieved
     * @return Returns the value of {@param key}, or nil when key does not exist..
     */
    String get(String key);

    /**
     * Returns the value associated with field in the hash stored at key.
     *
     * @param key   The key in which the field is located
     * @param field The field to be retrieved
     * @return Returns the value associated with field, or nil when field is not present in the hash or key does not exist..
     */
    String get(String key, String field);

    /**
     * Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
     * Any previous time to live associated with the key is discarded on successful SET operation.
     *
     * @param key   The name of the key to be created
     * @param value The value to be stored in {@param key}
     * @return OK if SET was executed correctly
     */
    String set(String key, String value);

    /**
     * Sets field in the hash stored at key to value. If key does not exist, a new key holding a hash is created.
     * If field already exists in the hash, it is overwritten.
     *
     * @param key   The name of the key to be created
     * @param field The name of the field to be created
     * @param value The value of the {@param field} field
     * @return The number of fields that were added.
     */
    Long set(String key, String field, String value);

    /**
     * Set key to hold the string value and set key to timeout after a given number of seconds.
     *
     * @param key    The name of the key to be created
     * @param expiry The time to live of the key {@param key}
     * @param value  The value of the key to be created
     * @return OK if SETEX was executed correctly.
     */
    String set(String key, int expiry, String value);

    /**
     * Matches all keys which follow a regex-like pattern
     *
     * @param pattern The regex pattern to be matched
     * @return Returns the number of keys deleted
     */
    Long pattern(String pattern);

    Long keys(String pattern);

    /**
     * Removes the specified keys. A key is ignored if it does not exist.
     *
     * @param key The key to be deleted.
     * @return The number of keys that were removed
     */
    Long delete(String key);

    /**
     * Removes the specified fields from the hash stored at key. Specified fields that do not exist within this hash are ignored.
     * If key does not exist, it is treated as an empty hash and this command returns 0.
     *
     * @param key   The name of the key to be queried
     * @param field The name of the field to be deleted
     * @return The number of fields that were removed from the hash, not including specified but non existing fields.
     */
    Long delete(String key, String field);

    String ping();

    /**
     * Expires the specified key
     *
     * @param key The key to be expired
     * @return Returns 1 if the timeout was successful, 0  if the key does not exist
     */
    Long expire(String key);

    /**
     * Expires the specified key
     *
     * @param key     The key to be expired
     * @param seconds The time to live
     */
    void expire(String key, Integer seconds);

    Boolean isExpiry();

    Long getExpiry();
}

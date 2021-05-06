package nz.co.fmg;

import nz.co.fmg.redis.Repository.RedisCommands;
import nz.co.fmg.redis.Repository.RedisRepository;

import java.util.Map;


// You need to have a working redis instance to test this class.
// The easiest way to set it up on windows is to use docker with a custom password configuration
// Once you have a working method add the '@Test' annotation above each method (don't add it to the constructor)
public class TestRedisCommands {
    private final RedisRepository redis;

    public TestRedisCommands() {
        redis = new RedisCommands("127.0.0.1", 6379L, "saxophone", 0L, false, false, 0L);
    }

    public void testRedisConnection() {
        String result = redis.ping();
        assert(result.equals("PONG"));
    }

    public void testSETCommands() {
        String result1 = redis.set("Key1", "unique_value_that's_not_copied");
        assert(result1.equals("OK"));
        String result2 = redis.set("Key2", 60, "you're an all star");
        assert(result2.equals("OK"));
        String result3 = redis.set("Key3:InnerKey:Value", "inner-key", "uh huh this my redis").toString();
        assert(result3.equals("1"));
        String result4 = redis.set("Key3:InnerKey:Value", "field", "wow, ive never seen a key this shiny before").toString();
        assert(result4.equals("1"));
        String result5 = redis.set("Key3:InnerKey:Value", "EraseMe", "not a bad song by").toString();
        assert(result5.equals("1"));
    }

    public void testGETCommands() {
        String result1 = redis.get("Key1");
        assert(result1.equals("unique_value_that's_not_copied"));
        String result2 = redis.get("Key2");
        assert(result2.equals("you're an all star"));
        String result3 = redis.get("Key3:InnerKey:Value", "inner-key");
        assert(result3.equals("uh huh this my redis"));
        Map<String, String> result4 = redis.getAll("Key3:InnerKey:Value");
        assert(result4.containsKey("field"));
    }

    public void testDELETECommands() {
        long result1 = redis.delete("Key1");
        assert(result1 == 1);
        long result2 = redis.delete("Key3:InnerKey:Value", "EraseMe");
        assert(result2 == 1);
        long result3 = redis.pattern("Key*");
        assert(result3 == 2);
        // set the keys again cause we need to test them
        redis.set("key1", "innerkey1", "val1");
        redis.set("key1", "innerkey2", "val2");
        redis.set("key1", "innerkey3", "val3");
        long result4 = redis.keys("key*");
        assert(result4 == 3);
    }
}

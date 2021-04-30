package nz.co.fmg;

import nz.co.fmg.redis.Repository.RedisCommands;
import org.junit.Test;

public class TestRedisCommands {
    private final RedisCommands redis;

    public TestRedisCommands() {
        redis = new RedisCommands("127.0.0.1", 6379L, "saxophone", 0L, false, false, 0L);
    }

    @Test
    public void TestSet() {
        redis.set("ping", "pong");
        redis.set("pong", "dong", "pong");
        redis.set("pong", "ping", "dong");
        redis.set("pingers", 900, "pongers");
    }

    @Test
    public void TestGet() {
        redis.get("ping");
        redis.get("pong", "dong");
        redis.getAll("dong");
    }

    @Test
    public void TestExpire() {
        redis.expire("pingers");
    }

    @Test
    public void TestDelete() {
        redis.delete("ping");
        redis.delete("pong", "dong");
        redis.pattern("p*");
        redis.hashPattern("p*");
    }
}

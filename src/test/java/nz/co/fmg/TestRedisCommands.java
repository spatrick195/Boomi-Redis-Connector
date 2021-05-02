package nz.co.fmg;

import nz.co.fmg.redis.Repository.RedisCommands;
import nz.co.fmg.redis.Repository.RedisRepository;

public class TestRedisCommands {
    private final RedisRepository redis;

    public TestRedisCommands() {
        redis = new RedisCommands("127.0.0.1", 6379L, "saxophone", 0L, false, false, 0L);
    }

    public void TestSet() {
        redis.set("ping", "pong");
        redis.set("pong", "dong", "pong");
        redis.set("pong", "ping", "dong");
        redis.set("pingers", 900, "pongers");
    }

    public void TestGet() {
        redis.get("ping");
        redis.get("pong", "dong");
        redis.getAll("dong");
    }

    public void TestExpire() {
        redis.expire("pingers");
    }

    public void TestDelete() {
        redis.delete("ping");
        redis.delete("pong", "dong");
        redis.pattern("p*");
        redis.hashPattern("p*");
    }
}

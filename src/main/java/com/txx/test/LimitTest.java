package com.txx.test;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class LimitTest implements ApplicationRunner {

    @Resource
    private RedisScript<List<Long>> rateLimitRedisScript;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void invoked() {
        // 设置lua脚本的ARGV的值
        List<String> scriptArgs = Arrays.asList(
                1 + "",
                3 + "",
                (Instant.now().toEpochMilli()) + "",
                "1");
        // 设置lua脚本的KEYS值
        List<String> keys = getKeys("test");
        List<Long> res = stringRedisTemplate.execute(rateLimitRedisScript,keys, scriptArgs.toArray());
        System.out.println("是否成功获得令牌：" + String.valueOf(res.get(0)) + "，剩余令牌数量:" + String.valueOf(res.get(1)) );
    }

    private List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    @Override
    public void run(ApplicationArguments args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.scheduleAtFixedRate(this::invoked, 0, 10, TimeUnit.MILLISECONDS);
        }
    }
}

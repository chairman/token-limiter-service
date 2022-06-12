package com.txx.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/executor")
public class MyLimiterServiceController {

    @Resource
    private RedisScript<List<Long>> rateLimitRedisScript;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 调用成功后会返回两个数，一个是是否成功标志，0代表限流，1代表未限流，还有一个是令牌桶中剩余的令牌数
     * @return
     */
    @GetMapping("/hello")
    public List<Long> userToken() {
        // 设置lua脚本的ARGV的值
        List<String> scriptArgs = Arrays.asList(
                1 + "",
                3 + "",
                (Instant.now().toEpochMilli()) + "",
                "1");
        // 设置lua脚本的KEYS值
        List<String> keys = getKeys("test");
        return stringRedisTemplate.execute(rateLimitRedisScript,keys, scriptArgs.toArray());
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
}

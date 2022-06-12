package com.txx.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import java.util.List;

@Configuration
public class LimiterConfig {

    @Bean(name = "rateLimitRedisScript")
    public RedisScript<List<Long>> rateLimitRedisScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/redis_token_rate_limit.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }
}

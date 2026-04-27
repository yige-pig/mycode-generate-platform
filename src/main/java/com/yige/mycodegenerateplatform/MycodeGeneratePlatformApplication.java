package com.yige.mycodegenerateplatform;


import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableCaching
public class MycodeGeneratePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycodeGeneratePlatformApplication.class, args);
    }

}

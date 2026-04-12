package com.yige.mycodegenerateplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class MycodeGeneratePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycodeGeneratePlatformApplication.class, args);
    }

}

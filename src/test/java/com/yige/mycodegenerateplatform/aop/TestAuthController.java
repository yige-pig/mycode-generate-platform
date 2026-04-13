package com.yige.mycodegenerateplatform.aop;

import com.yige.mycodegenerateplatform.annotation.AuthCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-auth")
public class TestAuthController {

    @GetMapping("/open")
    public String open() {
        return "ok-open";
    }

    @AuthCheck(mustRole = "admin")
    @GetMapping("/admin")
    public String adminOnly() {
        return "ok-admin";
    }
}


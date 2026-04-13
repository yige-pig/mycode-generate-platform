package com.yige.mycodegenerateplatform.aop;

import com.yige.mycodegenerateplatform.model.entity.User;
import com.yige.mycodegenerateplatform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthInterceptorIntegrationTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthInterceptorIntegrationTest {

    @Configuration//定义测试专用 Spring 配置
    @ComponentScan(basePackageClasses = {TestAuthController.class, AuthInterceptor.class, com.yige.mycodegenerateplatform.exception.GlobalExceptionHandler.class})
    @EnableAspectJAutoProxy
    //排除 DataSource 自动配置，避免测试启动时尝试连接真实 DB
    @EnableAutoConfiguration(excludeName = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"})
    static class TestConfig {
        @Bean
        public UserService userService() {
            //在上下文中注入一个 Mockito mock，替代 @MockBean 的方式，让测试可以对 getLoginUser(...) 进行 stub
            return org.mockito.Mockito.mock(UserService.class);
        }
    }

    @Autowired
    private WebApplicationContext wac;//注入完整的 Web 上下文（包含 MVC、拦截器、AOP、异常处理等）

    private MockMvc mockMvc;//用于发起模拟 HTTP 请求

    @Autowired
    private UserService userService; // the mock provided by TestConfig

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    /**
     * 验证没有 @AuthCheck 的接口不受拦截
     * @throws Exception
     */
    @Test
    public void openEndpoint_noAuth_required() throws Exception {
        mockMvc.perform(get("/test-auth/open"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok-open"));
    }

    @Test
    public void adminEndpoint_userIsUser_forbidden() throws Exception {
        User user = new User();
        user.setUserRole("user");
        org.mockito.Mockito.when(userService.getLoginUser(org.mockito.ArgumentMatchers.any())).thenReturn(user);

        mockMvc.perform(get("/test-auth/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40101))
                .andExpect(jsonPath("$.message").value("无权限"));
    }

    @Test
    public void adminEndpoint_userIsAdmin_ok() throws Exception {
        User admin = new User();
        admin.setUserRole("admin");
        org.mockito.Mockito.when(userService.getLoginUser(org.mockito.ArgumentMatchers.any())).thenReturn(admin);

        mockMvc.perform(get("/test-auth/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok-admin"));
    }

    @Test
    public void adminEndpoint_noLogin_forbidden() throws Exception {
        org.mockito.Mockito.when(userService.getLoginUser(org.mockito.ArgumentMatchers.any())).thenReturn(null);

        mockMvc.perform(get("/test-auth/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40101))
                .andExpect(jsonPath("$.message").value("无权限"));
    }
}

package com.sso.client1.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Client1 Controller
 */
@RestController
public class ClientController {

    /**
     * 首页 - 无需登录
     */
    @GetMapping("/")
    public String index() {
        return "欢迎来到 Client1 应用！";
    }

    /**
     * 需要登录才能访问
     */
    @GetMapping("/userinfo")
    public Map<String, Object> userinfo() {
        Map<String, Object> result = new HashMap<>();
        
        if (StpUtil.isLogin()) {
            result.put("code", 200);
            result.put("msg", "success");
            result.put("loginId", StpUtil.getLoginId());
            result.put("app", "client1");
            return result;
        }
        
        result.put("code", 401);
        result.put("msg", "未登录");
        return result;
    }

    /**
     * SSO 回调接口 - 用于接收 SSO 认证中心的登录凭证
     */
    @GetMapping("/sso/callback")
    public Map<String, Object> ssoCallback(String sso_token) {
        Map<String, Object> result = new HashMap<>();
        
        if (sso_token == null || sso_token.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "sso_token 不能为空");
            return result;
        }
        
        try {
            // 使用 SSO 令牌进行登录
            // 注意：实际项目中需要调用 SSO 服务器验证 token
            StpUtil.login(StpUtil.getLoginIdByToken(sso_token));
            
            result.put("code", 200);
            result.put("msg", "SSO 登录成功");
            result.put("token", StpUtil.getTokenValue());
            return result;
        } catch (Exception e) {
            result.put("code", 401);
            result.put("msg", "SSO 登录失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 跳转到 SSO 认证中心登录
     */
    @GetMapping("/toLogin")
    public String toLogin() {
        // 跳转到 SSO 认证中心
        String authUrl = "http://localhost:8000/sso/doLogin?username=admin&password=123456&service=http://localhost:8001/sso/callback";
        return "redirect:" + authUrl;
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();
        
        StpUtil.logout();
        result.put("code", 200);
        result.put("msg", "退出成功");
        return result;
    }
}

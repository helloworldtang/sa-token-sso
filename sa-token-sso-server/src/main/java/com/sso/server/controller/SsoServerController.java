package com.sso.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SSO 认证中心 Controller
 */
@RestController
@RequestMapping("/sso/")
public class SsoServerController {

    /**
     * SSO 登录接口
     * @param username 用户名
     * @param password 密码
     * @param service 回调服务地址（可选）
     * @return 登录结果
     */
    @PostMapping("doLogin")
    public Map<String, Object> doLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String service) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证账号密码（模拟）
        if ("admin".equals(username) && "123456".equals(password)) {
            // 2. 创建登录会话
            StpUtil.login(1);
            String token = StpUtil.getTokenValue();
            
            // 3. 返回 token
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token);
            
            // 4. 如果有 service 参数，构建回调地址
            if (service != null && !service.isEmpty()) {
                String redirectUrl = service + "?sso_token=" + token;
                result.put("redirect", redirectUrl);
            }
            
            return result;
        }
        
        // 登录失败
        result.put("code", 401);
        result.put("msg", "账号或密码错误");
        return result;
    }

    /**
     * 验证 Token 是否有效
     */
    @GetMapping("verify")
    public Map<String, Object> verify(String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证 token 有效性
            if (token != null && StpUtil.getLoginIdByToken(token) != null) {
                result.put("code", 200);
                result.put("msg", "验证成功");
                result.put("loginId", StpUtil.getLoginIdByToken(token));
                return result;
            }
        } catch (Exception e) {
            // token 无效
        }
        
        result.put("code", 401);
        result.put("msg", "token无效或已过期");
        return result;
    }

    /**
     * 获取用户信息
     */
    @GetMapping("getUserinfo")
    public Map<String, Object> getUserinfo() {
        Map<String, Object> result = new HashMap<>();
        
        if (StpUtil.isLogin()) {
            result.put("code", 200);
            result.put("msg", "success");
            result.put("loginId", StpUtil.getLoginId());
            result.put("username", "admin");
            result.put("nickname", "管理员");
            return result;
        }
        
        result.put("code", 401);
        result.put("msg", "未登录");
        return result;
    }
}

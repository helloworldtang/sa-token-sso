# Sa-Token SSO 示例项目

🎯 一个基于 Sa-Token 实现的 **SSO 单点登录** 示例项目，适合学习和小规模部署。

![Java](https://img.shields.io/badge/Java-17+-green)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Sa-Token](https://img.shields.io/badge/Sa--Token-1.37.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 简介

本项目演示了如何使用 **Sa-Token** 快速实现 **SSO 单点登录** 功能。

### 什么是 SSO？

**SSO**（Single Sign-On，单点登录）= 一次登录，处处访问。

想象一下：
- 你登录了淘宝
- 然后打开天猫、盒马、飞猪
- 不用再重复登录，直接可用

这就是 SSO。

---

## 项目结构

```
sa-token-sso/
├── sa-token-sso-server/      # SSO 认证中心 (端口 8000)
├── sa-token-sso-client1/     # 应用1 (端口 8001)
└── sa-token-sso-client2/      # 应用2 (端口 8002)
```

| 模块 | 端口 | 说明 |
|------|------|------|
| sa-token-sso-server | 8000 | SSO 认证中心，负责用户登录 |
| sa-token-sso-client1 | 8001 | 示例应用1 |
| sa-token-sso-client2 | 8002 | 示例应用2 |

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/helloworldtang/sa-token-sso.git
cd sa-token-sso
```

### 2. 编译项目

```bash
mvn clean compile -DskipTests
```

### 3. 启动服务

分别启动三个模块：

```bash
# 终端1：启动 SSO 认证中心
cd sa-token-sso-server
mvn spring-boot:run

# 终端2：启动应用1
cd sa-token-sso-client1
mvn spring-boot:run

# 终端3：启动应用2
cd sa-token-sso-client2
mvn spring-boot:run
```

启动成功后访问：
- SSO 认证中心：http://localhost:8000
- 应用1：http://localhost:8001
- 应用2：http://localhost:8002

---

## 核心代码

### SSO 认证中心（Server）

```java
@PostMapping("doLogin")
public Map<String, Object> doLogin(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam(required = false) String service) {
    
    // 1. 验证账号密码
    if ("admin".equals(username) && "123456".equals(password)) {
        
        // 2. 创建登录会话
        StpUtil.login(1);
        String token = StpUtil.getTokenValue();
        
        // 3. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "登录成功");
        result.put("token", token);
        
        // 4. 回调地址
        if (service != null) {
            result.put("redirect", service + "?sso_token=" + token);
        }
        return result;
    }
    
    return Map.of("code", 401, "msg", "账号或密码错误");
}
```

### 客户端回调（Client）

```java
@GetMapping("/sso/callback")
public Map<String, Object> ssoCallback(String sso_token) {
    // 使用 SSO 令牌登录
    StpUtil.login(StpUtil.getLoginIdByToken(sso_token));
    
    return Map.of(
        "code", 200,
        "msg", "SSO 登录成功",
        "token", StpUtil.getTokenValue()
    );
}
```

---

## 测试 SSO

1. 访问 `http://localhost:8001/userinfo` → 返回"未登录"
2. 访问 `http://localhost:8001/toLogin` → 自动跳转到 SSO 认证中心
3. 输入账号 `admin`，密码 `123456` → 登录成功
4. 自动跳转回应用1，已登录 ✅
5. 再访问 `http://localhost:8002/userinfo` → 直接显示已登录 ✅

---

## 为什么选 Sa-Token？

| 对比项 | Sa-Token | Spring Security |
|--------|----------|-----------------|
| 学习成本 | ⭐ 极低 | ⭐⭐⭐⭐⭐ |
| 文档 | 中文详尽 | 英文难懂 |
| SSO 支持 | 天然支持 | 需额外配置 |
| 体积 | 轻量 | 重量级 |
| 社区 | 活跃 | 一般 |

> 我的选择：**Sa-Token** - 简单、好用、国产之光！

---

## 技术栈

- **Java** 17+
- **Spring Boot** 3.2.0
- **Sa-Token** 1.37.0
- **Maven** 3.9+

---

## 参考资料

- [Sa-Token 官方文档](https://sa-token.dev33.cn/)
- [Sa-Token SSO 文档](https://sa-token.dev33.cn/doc.html#/sso/sso-server)
- [OAuth 2.0 协议规范](https://datatracker.ietf.org/doc/html/rfc6749)

---

## 许可证

MIT License - 欢迎 Star ⭐

---

## 公众号

本文同步发布于公众号「**码骨丹心**」，专注分享后端技术、面试干货。

![码骨丹心](https://img.shields.io/badge/欢迎关注-码骨丹心-red)

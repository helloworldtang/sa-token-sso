# 面试官：你能手写一个 SSO 单点登录流程吗？

> 面试官：你来说说什么是 OAuth2.0？什么是 SSO？
> 
> 我：...
> 
> 面试官：那你能手写一下 SSO 的登录流程吗？
> 
> 我：...

---

相信很多同学面试时都被问过 SSO 相关问题。今天这篇文章，就来讲清楚 **OAuth2.0 和 SSO** 到底是什么，以及如何从零实现一个 SSO 系统。

---

## 一、为什么面试官爱问 SSO？

在面试中，**登录体系**是高频考点：

- 你说项目用了 Shiro？→ 面试官：太老了
- 你说项目用了 Spring Security？→ 面试官：配置太复杂
- 你说项目用了 Sa-Token？→ 面试官：这个轻量，我问问...

**Sa-Token** 作为国产轻量级权限框架，现在越来越火。它**开箱即用、文档全、社区活跃**，关键是**天然支持 SSO**！

> 悄悄说一句：Sa-Token 的作者还在持续更新，比某些"古老"框架香多了 👇

![Sa-Token](https://img.shields.io/badge/⭐-Stars-13k+-blue) ![Sa-Token](https://img.shields.io/badge/📚-文档-详尽-green)

---

## 二、OAuth2.0 是什么？

### 2.1 官方定义

> OAuth 2.0（Open Authorization）是一个**授权协议**，允许用户授权第三方应用访问他在另一个服务提供商上的资源，而**不需要提供用户名密码**。

用人话说：**微信授权登录**、**支付宝快捷登录**，背后都是 OAuth2.0。

### 2.2 解决了什么问题？

| 痛点 | OAuth2.0 解决方案 |
|------|------------------|
| 第三方登录要密码？ | ✅ 只授权访问权限，不给密码 |
| 授权后能无限访问？ | ✅ 支持 scope 范围限制 |
| 授权一直有效？ | ✅ 支持 token 过期和刷新 |

---

## 三、OAuth2.0 的四个核心角色

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   用户 (Resource Owner)                                     │
│       │                                                     │
│       ▼                                                     │
│   第三方应用 (Client) ──────▶ 授权服务 (Auth Server)         │
│                                     │                       │
│                                     ▼                       │
│                                   资源服务                   │
│                                 (Resource Server)            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

| 角色 | 说明 | 举例 |
|------|------|------|
| **用户** | 数据的拥有者 | 你、我 |
| **第三方应用** | 请求访问的应用程序 | 抖音、知乎 |
| **授权服务** | 负责发放令牌 | 微信授权服务器 |
| **资源服务** | 存储受保护的资源 | 微信的用户数据 |

---

## 四、OAuth2.0 授权流程（必背）

### 4.1 完整流程图

```
用户 ──▶ 第三方应用 ──▶ 授权服务 ──▶ 资源服务
 │        │                │              │
 │        │ 1.点击登录    │              │
 │        │───────────────▶│              │
 │        │                │              │
 │        │ 2.跳转授权页  │              │
 │        │◀──────────────│              │
 │        │                │              │
 │        │ 3.确认授权    │              │
 │        │───────────────▶│              │
 │        │                │              │
 │        │ 4.返回授权码  │              │
 │        │◀──────────────│              │
 │        │                │              │
 │        │ 5.用授权码换令牌             │
 │        │───────────────▶│              │
 │        │                │              │
 │        │ 6.返回 access_token          │
 │        │◀──────────────│              │
 │        │                │              │
 │        │ 7.携带令牌请求资源           │
 │        │────────────────────────────────▶
 │        │                │              │
 │        │ 8.返回受保护资源             │
 │        │◀────────────────────────────────
```

### 4.2 面试必背：授权码模式

> 面试官：OAuth2.0 的授权码模式了解吗？
> 
> 我：了解！一共 4 步：

**第一步：用户授权**
```
第三方应用跳转到授权服务器，用户点击"同意授权"
```

**第二步：返回授权码**
```
授权服务器返回授权码 code：
http://myapp.com/callback?code=SplxlOBeZQQYbYS6WxSbIA
```

**第三步：换令牌**
```
用授权码换 access_token：
POST /token
grant_type=authorization_code
code=SplxlOBeZQQYbYS6WxSbIA
```

**第四步：访问资源**
```
携带令牌访问资源：
GET /api/userinfo
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
```

---

## 五、SSO 单点登录是什么？

### 5.1 定义

> SSO（Single Sign-On，单点登录）= **一次登录，处处访问**

### 5.2 解决了什么问题？

| 痛点 | SSO 解决方案 |
|------|-------------|
| 每个系统都要登录？ | 一次登录，处处访问 |
| 密码记不住？ | 记一个密码就够了 |
| 管理员维护多账号？ | 统一管理 |

### 5.3 适用场景

```
┌─────────────────────────────────────────────┐
│              企业内部系统                     │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  │
│  │ OA系统│  │ 钉钉  │  │ 飞书  │  │ 邮件  │  │
│  └──────┘  └──────┘  └──────┘  └──────┘  │
│       ▲         ▲         ▲         ▲       │
│       └─────────┴─────────┴─────────┘       │
│                     │                        │
│              ┌──────┴──────┐                 │
│              │   SSO中心   │                 │
│              └─────────────┘                 │
└─────────────────────────────────────────────┘
```

---

## 六、SSO 数据流转（手写版）

### 6.1 首次登录

```
1. 用户访问 app1.com → 发现未登录
2. 跳转到 SSO 登录页 (sso.com/login?service=app1.com)
3. 用户输入账号密码
4. SSO 验证通过，写入 TGT Cookie（种在 SSO 域名下）
5. 重定向回 app1.com?ticket=ST-xxx
6. app1.com 用 ticket 换取 token
7. 登录成功！
```

### 6.2 访问第二个系统

```
1. 用户访问 app2.com → 发现未登录
2. 跳转到 SSO (sso.com/login?service=app2.com)
3. SSO 发现 TGT Cookie 有效
4. 直接返回 ticket（无需再输密码！）
5. app2.com 用 ticket 换取 token
6. 登录成功！✅
```

---

## 七、实战：基于 Sa-Token 实现 SSO

### 7.1 为什么选 Sa-Token？

| 对比项 | Sa-Token | Spring Security |
|-------|----------|-----------------|
| 学习成本 | ⭐ 极低 | ⭐⭐⭐⭐⭐ |
| 文档 | 中文详尽 | 英文+难懂 |
| 集成复杂度 | ⭐ 引入即用 | 配置繁琐 |
| SSO 支持 | ⭐ 天然支持 | 需额外配置 |
| 体积 | 轻量 | 重量级 |

> 我的选择：**Sa-Token** - 简单、好用、国产之光！✨

### 7.2 项目结构

```
sa-token-sso/
├── sa-token-sso-server/      # SSO 认证中心 (端口 8000)
├── sa-token-sso-client1/      # 应用1 (端口 8001)
└── sa-token-sso-client2/      # 应用2 (端口 8002)
```

### 7.3 核心代码

#### SSO 认证中心（Server）

```java
@RestController
@RequestMapping("/sso/")
public class SsoServerController {

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
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token);
            
            // 3. 回调地址
            if (service != null) {
                result.put("redirect", service + "?sso_token=" + token);
            }
            return result;
        }
        
        return Map.of("code", 401, "msg", "账号或密码错误");
    }
}
```

#### 客户端（Client）

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

### 7.4 完整项目

> 📢 **完整代码已上传 GitHub**：https://github.com/helloworldtang/sa-token-sso

---

## 八、面试总结

### 8.1 必背知识点

| 知识点 | 面试话术 |
|--------|---------|
| OAuth2.0 | 一个授权协议，用于让第三方安全获取用户授权 |
| 四种角色 | 资源所有者、客户端、授权服务器、资源服务器 |
| 授权码流程 | 授权码 → 换令牌 → 访问资源 |
| SSO | 单点登录，一次登录多处访问 |
| 核心机制 | TGT + Ticket 机制 |

### 8.2 反问面试官

> 面试官：还有什么要补充的吗？

> 我：我想了解一下，咱们公司用的是哪种 SSO 方案？是自研的还是基于 CAS、OAuth2.0 的？

---

## 九、参考资料

- [Sa-Token 官方文档](https://sa-token.dev33.cn/)
- [OAuth 2.0 协议规范](https://datatracker.ietf.org/doc/html/rfc6749)
- 完整项目代码：https://github.com/helloworldtang/sa-token-sso

---

**💬 你在面试中被问过 SSO 吗？欢迎评论区分享你的面试经历！**

**⭐ 如果这篇文章对你有帮助，记得点赞、在看、转发！**

*本文完，欢迎 Star⭐*

---

> 📌 **往期精选**
> - [Spring Boot 3 权限框架推荐：Sa-Token]()
> - [一文讲透 JWT 鉴权]()
> - [ Shiro 太老了，试试 Sa-Token]()

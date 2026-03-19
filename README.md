# 面试必备：一文讲透 OAuth2.0 与 SSO

> 面试官：同学，你来说说什么是 OAuth2.0？什么是 SSO？
> 
> 我：...

---

## 一、OAuth2.0 是什么？

### 1.1 面试官为什么要问这个？

在面试中，OAuth2.0 是高频考点，尤其是对于做**登录体系、第三方登录、企业内部系统**的同学来说，这是必备知识点。

### 1.2 官方定义

> OAuth 2.0（Open Authorization）是一个**授权协议**，允许用户授权第三方应用访问他在另一个服务提供商上的资源，而无需将用户名密码提供给第三方应用。

用人话说：**微信授权登录**、**支付宝快捷登录**，背后都是 OAuth2.0。

### 1.3 解决了什么问题？

| 场景 | 痛点 | OAuth2.0 解决方案 |
|------|------|------------------|
| 第三方登录 | 要把账号密码给第三方？不安全！ | 只授权访问权限，不给密码 |
| 授权第三方访问 | 第三方可以无限访问所有数据？ | 支持**scope（范围）**限制 |
| 授权过期 | 授权一次，长期有效？ | 支持**token过期**和**刷新** |

---

## 二、OAuth2.0 的核心角色

OAuth2.0 中有**四个核心角色**：

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   ┌──────────┐     ┌──────────────┐     ┌──────────────┐   │
│   │   用户   │────▶│  第三方应用   │────▶│  授权服务    │   │
│   │(Resource │     │(Client)      │     │(Auth Server) │   │
│   │ Owner)   │     │              │     │              │   │
│   └──────────┘     └──────────────┘     └──────────────┘   │
│                                                │            │
│                                                ▼            │
│                                         ┌──────────────┐    │
│                                         │  资源服务    │    │
│                                         │(Resource    │    │
│                                         │ Server)      │    │
│                                         └──────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

| 角色 | 说明 | 举例 |
|------|------|------|
| **Resource Owner（资源所有者）** | 数据的拥有者 | 你、我 |
| **Client（第三方应用）** | 请求访问的应用程序 | 抖音、知乎 |
| **Auth Server（授权服务）** | 负责发放令牌 | 微信授权服务器 |
| **Resource Server（资源服务）** | 存储受保护的资源 | 微信的用户数据 |

---

## 三、OAuth2.0 授权流程

### 3.1 完整流程图

```
┌─────────┐     ┌──────────┐     ┌──────────────┐     ┌──────────────┐
│  用户   │     │ 第三方应用│     │  授权服务    │     │  资源服务    │
└────┬────┘     └────┬─────┘     └──────┬───────┘     └──────┬───────┘
     │               │                   │                    │
     │ 1.点击登录   │                   │                    │
     │─────────────▶│                   │                    │
     │               │ 2.跳转授权页      │                    │
     │◀─────────────│                   │                    │
     │               │                   │                    │
     │ 3.确认授权   │                   │                    │
     │─────────────▶│                   │                    │
     │               │ 4.授权码返回      │                    │
     │◀─────────────│                   │                    │
     │               │                   │                    │
     │               │ 5.用授权码换令牌  │                    │
     │               │───────────────────▶│                    │
     │               │                   │                    │
     │               │ 6.返回访问令牌    │                    │
     │               │◀──────────────────│                    │
     │               │                   │                    │
     │               │ 7.携带令牌请求资源 │                    │
     │               │───────────────────────────────────────▶│
     │               │                   │                    │
     │               │ 8.返回受保护资源  │                    │
     │               │◀───────────────────────────────────────│
```

### 3.2 详细步骤

**第一步：用户点击登录**
```
用户访问第三方应用，点击"微信登录"
第三方应用跳转到授权服务器
```

**第二步：跳转授权页**
```
授权服务器显示授权页面，询问用户：
"xxx 应用请求获取你的以下权限：
- 获取基本信息
- 获取好友列表
用户点击"同意"
```

**第三步：返回授权码**
```
授权服务器生成授权码(code)，重定向回第三方应用

http://myapp.com/callback?code=SplxlOBeZQQYbYS6WxSbIA
```

**第四步：换令牌**
```
第三方应用用授权码，换取访问令牌(access_token)

POST https://auth.server.com/token
grant_type=authorization_code
code=SplxlOBeZQQYbYS6WxSbIA
client_id=myapp
client_secret=xxxx
```

**第五步：访问资源**
```
第三方应用携带 access_token 访问资源服务器

GET /api/user/info
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 四、OAuth2.0 的四种授权模式

| 模式 | 适用场景 | 安全性 |
|------|---------|--------|
| **授权码模式** | Web端、移动端最推荐 | ⭐⭐⭐⭐⭐ |
| **简化模式** | 纯前端SPA | ⭐⭐⭐ |
| **密码模式** | 高度可信的第一方应用 | ⭐⭐ |
| **客户端模式** | 机器到机器通信 | ⭐⭐⭐ |

> 面试技巧：大部分公司用的是**授权码模式**，能不说密码模式就不说（不安全）。

---

## 五、SSO 是什么？

### 5.1 定义

> SSO（Single Sign-On，单点登录）是一种**身份认证机制**，允许用户只登录一次，就能访问所有相互信任的应用系统。

### 5.2 解决了什么问题？

| 痛点 | SSO 解决方案 |
|------|-------------|
| 每个系统都要单独注册/登录？ | 一次登录，处处访问 |
| 密码记不住？ | 记一个密码就够了 |
| 管理员维护多个账号？ | 统一管理 |

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
│              │  (统一登录)  │                 │
│              └─────────────┘                 │
└─────────────────────────────────────────────┘
```

**典型场景：**
1. 企业内部多套系统（OA、CRM、ERP）
2. 集团旗下多个子公司产品
3. 开放平台生态

---

## 六、SSO 数据流转详解

### 6.1 用户首次登录

```
用户访问 app1.com
    │
    ▼
app1.com 发现没有登录态
    │
    ▼
跳转 SSO 登录页面 (sso.com/login?service=app1.com)
    │
    ▼
用户输入账号密码
    │
    ▼
SSO 服务器验证通过
    │
    ▼
SSO 服务器生成 TGT (Ticket Granting Ticket)
写入 SSO 服务器域名下的 Cookie
    │
    ▼
重定向回 app1.com?ticket=ST-xxx
    │
    ▼
app1.com 用 ticket 换取用户信息
    │
    ▼
登录成功，设置 app1.com 的 Session
```

### 6.2 用户访问另一个系统

```
用户访问 app2.com
    │
    ▼
app2.com 发现没有登录态
    │
    ▼
跳转 SSO 服务器 (sso.com/login?service=app2.com)
    │
    ▼
SSO 服务器发现 TGT Cookie 有效
    │
    ▼
直接返回 ticket 给 app2.com
    │
    ▼
app2.com 用 ticket 换取用户信息
    │
    ▼
登录成功，无需再次输入密码！
```

### 6.3 核心交互流程图

```
┌─────────┐   1.无态   ┌─────────┐   2.跳转   ┌─────────┐
│ app1.com│───────────▶│ app2.com│───────────▶│sso.com  │
└─────────┘            └─────────┘            └────┬────┘
     │                                            │
     │ 3.无登录态                                 │
     │ ─────────────────────────────────────────▶│
     │                                            │
     │ 4.跳转SSO登录页                           │
     │ ◀───────────────────────────────────────── │
     │                                            │
     │ 5.输入账号密码                            │
     │ ─────────────────────────────────────────▶│
     │                                            │
     │ 6.验证通过，写TGT Cookie                   │
     │ ◀───────────────────────────────────────── │
     │                                            │
     │ 7.返回ticket=ST-xxx                       │
     │ ◀───────────────────────────────────────── │
     │                                            │
     │ 8.用ticket换token                         │
     │ ──────────────────────────────────────────▶│
     │                                            │
     │ 9.返回用户信息                            │
     │ ◀───────────────────────────────────────── │
     │                                            │
     │ 10.登录成功                               │
```

---

## 七、实战：基于 SA-Token 实现 SSO

### 7.1 技术选型

**为什么选 SA-Token 而不是 Spring Security？**

| 对比项 | SA-Token | Spring Security |
|-------|----------|-----------------|
| 学习成本 | ⭐ 极低 | ⭐⭐⭐⭐⭐ |
| 文档 | 中文详尽 | 英文+难懂 |
| 集成复杂度 | ⭐ 引入即用 | 配置繁琐 |
| SSO 支持 | ⭐ 天然支持 | 需要额外配置 |
| 社区 | 国内活跃 | 国外为主 |
| 体积 | 轻量 | 重量级 |

> 我的选择：**SA-Token** - 简单、好用、文档全、国产！

### 7.2 项目结构

```
sa-token-sso/
├── sa-token-sso-server/      # SSO 认证中心 (端口 8000)
├── sa-token-sso-client1/      # 应用1 (端口 8001)
├── sa-token-sso-client2/      # 应用2 (端口 8002)
└── README.md
```

### 7.3 核心代码

#### 7.3.1 SSO 认证中心（Server）

```java
// SaTokenSsoConfig.java
@Configuration
public class SaTokenSsoConfig {
    
    @Autowired
    private SsoDaoListener ssoDaoListener;
    
    @Autowired
    private SsoService ssoService;
    
    @Bean
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedis(); // 使用 Redis 存储
    }
    
    @Bean
    public SsoHandleInterceptor ssoHandleInterceptor() {
        return new SsoHandleInterceptor();
    }
}
```

```java
// SSO Controller
@RestController
@RequestMapping("/sso/")
public class SsoServerController {
    
    /**
     * SSO 认证中心 - 登录接口
     */
    @PostMapping("doLogin")
    public JsonResult<String> doLogin(String username, String password, 
                                       String service, HttpServletResponse response) {
        // 1. 验证账号密码
        if ("admin".equals(username) && "123456".equals(password)) {
            // 2. 创建登录会话
            StpUtil.login(1);
            String token = StpUtil.getTokenValue();
            
            // 3. 如果有 service 参数，跳转到应用
            if (StringUtils.isNotBlank(service)) {
                String redirectUrl = service + "?sso_token=" + token;
                return JsonResult.success(redirectUrl);
            }
            return JsonResult.success(token);
        }
        return JsonResult.errorMsg("账号或密码错误");
    }
}
```

#### 7.3.2 应用端（Client）

```java
// SaTokenSsoConfig.java - 应用端配置
@Configuration
public class SaTokenSsoConfig {
    
    @Bean
    public SaSsoHandleInterceptor saSsoHandleInterceptor() {
        return new SaSsoHandleInterceptor();
    }
    
    @Bean
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedis();
    }
}
```

```java
// 配置 SSO 认证中心地址
sa-token:
  sso:
    # SSO 认证中心地址
    auth-url: http://localhost:8000/sso/doLogin
    # 回调地址
    callback-url: http://localhost:8001/sso/callback
    # 是否开启 SSO 模式
    is-sso: true
```

### 7.4 应用接入 SSO

```java
// Client 应用 Controller
@RestController
public class ClientController {
    
    /**
     * 需要登录才能访问的接口
     */
    @SaLoginCheck
    @GetMapping("/userinfo")
    public JsonResult<JSONObject> userinfo() {
        // 获取当前登录用户信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return JsonResult.success(StpUtil.getSession().getModel("user", JSONObject.class));
    }
    
    /**
     * SSO 回调接口
     */
    @GetMapping("/sso/callback")
    public JsonResult<?> ssoCallback(String sso_token) {
        // 使用 SSO 令牌进行登录
        SaSsoUtil.login(sso_token);
        return JsonResult.success("登录成功");
    }
}
```

### 7.5 完整项目

> 完整代码已上传至 GitHub：https://github.com/helloworldtang/sa-token-sso

---

## 八、面试总结

### 8.1 必背知识点

| 知识点 | 面试话术 |
|--------|---------|
| OAuth2.0 定义 | 一个授权协议，用于让第三方应用安全地获取用户授权 |
| 四种角色 | 资源所有者、客户端、授权服务器、资源服务器 |
| 授权码流程 | 授权码 → 换令牌 → 访问资源 |
| SSO 定义 | 单点登录，一次登录多处访问 |
| SSO 核心 | TGT（Ticket Granting Ticket）+ Ticket 机制 |

### 8.2 反问面试官的话术

> 面试官：还有什么要补充的吗？

> 我：我想了解一下，咱们公司用的是哪种 SSO 方案？是自研的还是基于 CAS、OAuth2.0 的？

---

## 九、参考资料

- [SA-Token 官方文档](https://sa-token.dev33.cn/)
- [OAuth 2.0 协议规范](https://datatracker.ietf.org/doc/html/rfc6749)
- 完整项目代码：https://github.com/helloworldtang/sa-token-sso

---

> 📌 **面试TIP**：手写 SSO 流程图是加分项！建议在面试纸上快速画出用户→应用1→SSO→应用2 的完整流程。

*本文完，欢迎 Star⭐*

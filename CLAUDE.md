# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

`bank-system/` — 全栈银行管理系统，包含客户管理、授信管理、合同管理三个已完成模块，以及 RBAC 权限体系。根目录另有一份 Java 面试题 Markdown 文件。

## 常用命令

### 前端（`bank-system/frontend/`）

```bash
npm run dev        # 启动 Vite 开发服务器 :5173，/api 代理到 :8080
npm run build      # 类型检查 + 生产构建
npm run preview    # 预览生产构建
```

### 后端（`bank-system/backend/`）

```bash
mvn spring-boot:run                                # 启动 Spring Boot :8080
mvn test                                            # 运行全部测试（目前无测试用例）
mvn test -Dtest=ClassName                           # 运行单个测试类
```

**环境依赖**：JDK 17+、MySQL 8+。先执行 `src/main/resources/sql/init.sql` 初始化数据库（创建库 `bank_system`、建表、插入 admin/123456 和管理菜单）。

## 架构

### 后端 — Spring Boot 3.2 + MyBatis-Plus 3.5 + JWT

分层结构：

| 包 | 职责 |
|---|---|
| `controller/` | REST 接口。早期模块（Auth、Customer）Controller 直接调 Mapper；后续模块（Credit、Contract）走 Service 层 |
| `service/` + `service/impl/` | 业务逻辑层 |
| `mapper/` | 继承 MyBatis-Plus `BaseMapper<T>`，零 XML，全部用 `LambdaQueryWrapper` 构建查询 |
| `entity/` | 数据库实体（`@TableName` + `@TableField`） |
| `dto/` | 请求体对象，使用 `@Valid` + Jakarta 校验注解 |
| `vo/` | 响应对象 |
| `common/` | `Result<T>` 统一响应信封、`ResultCode` 状态码枚举、`BusinessException`（抛出自定义 code+message）、`GlobalExceptionHandler`（`@RestControllerAdvice` 全局异常拦截） |
| `security/` | `JwtTokenProvider`（jjwt 0.12，HS256，86400s 过期，`Bearer ` 前缀）、`JwtAuthenticationFilter`（OncePerRequestFilter）、`UserDetailsServiceImpl` |
| `config/` | `SecurityConfig`（除 `/api/auth/login` 外全部需认证，无状态会话）、`MyBatisPlusConfig`（自动填充 createTime/updateTime、分页插件、逻辑删除）、`CorsConfig`（允许所有来源和方法） |

**核心模式**：
- 所有 API 返回 `Result<T>`，包含 `code`/`message`/`data`
- 错误码统一用 `ResultCode` 枚举（200 成功、401 未授权、403 无权限、404 不存在、409 冲突、500 内部错误）
- 业务异常抛 `BusinessException`，由 `GlobalExceptionHandler` 统一转为 `Result`
- JWT：username 为 subject，HS256 签名，Authorization 头携带 `Bearer <token>`
- 数据库表统一使用 `create_time`/`update_time`/`deleted` 列，MyBatis-Plus 自动管理

### 前端 — Vue 3 + TypeScript + Vite + Element Plus

```
src/
├── api/          # 按模块拆分的 Axios 请求函数 + request.ts（拦截器）
├── stores/       # Pinia 状态管理：auth（鉴权/菜单）、app（侧边栏折叠）、tabs（多标签页）
├── router/       # 静态路由配置 + beforeEach 鉴权守卫 + afterEach 标签同步
├── views/        # 按模块目录存放 SFC：layout/ login/ dashboard/ customer/ credit/ contract/ system/
├── utils/        # auth.ts — 基于 localStorage 的 token 读写
└── styles/       # theme.css（Element Plus 暗色主题 + 品牌色覆盖）、global.css
```

**核心模式**：
- `request.ts` 拦截器：请求自动附加 `Bearer` token；响应 code≠200 提示错误，401 清除 token 跳转 /login
- `authStore` 管理 token/userInfo/menus，支持从后端菜单树动态生成前端路由
- `tabsStore` 实现多标签页（打开、关闭、关闭其他/左侧/右侧/全部），通过 router `afterEach` 自动同步，支持 `keep-alive` 缓存
- `appStore` 管理侧边栏折叠状态
- 所有 Element Plus 图标在 `main.ts` 中全局注册
- `@` 路径别名 → `src/`
- 默认暗色模式（`<html class="dark">`，CSS 变量在 `theme.css` 中定义）
- Vite 开发代理 `/api` → `http://localhost:8080`

### 数据库

MySQL `bank_system`，初始化脚本在 `backend/src/main/resources/sql/init.sql`。

核心表：`sys_user`、`sys_role`、`sys_menu`、`sys_user_role`、`sys_role_menu`（RBAC 五表）、`customer`（客户）、`credit_application`（授信申请）、`credit_approval`（授信批复）、`contract`（合同）。另有 `account`、`transaction_record`、`loan` 三张表已建但前端尚未对接。

默认管理员：`admin` / `123456`（BCrypt 加密）。

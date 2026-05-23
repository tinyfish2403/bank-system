# 银行系统需求文档

> 适用场景：交给 Claude Code 从零实现完整的前后端分离项目
> 整理时间：2026 年 5 月

---

## 目录

- [一、项目概述](#一项目概述)
- [二、技术栈与开发环境](#二技术栈与开发环境)
- [三、功能模块规划](#三功能模块规划)
- [四、数据库设计（含完整 DDL）](#四数据库设计含完整-ddl)
- [五、Spring Security + JWT 认证方案](#五spring-security--jwt-认证方案)
- [六、统一响应格式](#六统一响应格式)
- [七、客户管理模块详细设计](#七客户管理模块详细设计)
- [八、前端路由与布局设计](#八前端路由与布局设计)
- [九、项目结构与包结构](#九项目结构与包结构)
- [十、关键配置文件](#十关键配置文件)
- [十一、异常处理与通用组件](#十一异常处理与通用组件)
- [十二、开发与部署说明](#十二开发与部署说明)
- [十三、前端 UI 升级（科技风 + 标签页导航）](#十三前端-ui-升级科技风--标签页导航)

---

## 一、项目概述

### 1.1 项目名称

Simple Bank System — 简易银行后台管理系统

### 1.2 项目背景

为银行柜员和客户经理提供一套内部管理系统，支持客户信息管理、账户管理、交易管理、贷款管理等核心业务操作。

### 1.3 项目目标

- 实现客户信息的 CRUD 管理
- 支持 RBAC 权限控制，不同角色看到不同菜单
- 前后端分离架构，便于独立开发和部署
- 为后续模块（账户、交易、贷款）预留扩展入口

### 1.4 技术架构总览

```
浏览器 ─── Vue 3 前端 (port 5173) ─── HTTP/REST ─── Spring Boot 后端 (port 8080) ─── MySQL 8.0 (port 3306)
```

- 前端负责页面渲染与用户交互，通过 Axios 调用后端 REST API
- 后端负责业务逻辑、权限校验、数据持久化
- 认证方式：JWT（JSON Web Token），无状态会话

---

## 二、技术栈与开发环境

### 2.1 技术选型

| 层级 | 技术 | 版本要求 |
|------|------|---------|
| 前端框架 | Vue 3 (Composition API) | 3.4+ |
| 构建工具 | Vite | 5.x |
| UI 组件库 | Element Plus | 2.x |
| 状态管理 | Pinia | 2.x |
| 路由 | Vue Router | 4.x |
| HTTP 客户端 | Axios | 1.x |
| 后端框架 | Spring Boot | 3.2+ |
| ORM | MyBatis-Plus | 3.5.x |
| 安全框架 | Spring Security + JWT | - |
| 数据库 | MySQL | 8.0.x |
| 构建工具 | Maven | 3.9.x |
| JDK | Java | 17.0.x |
| Node.js | - | 20.x |

### 2.2 本地开发环境要求

- **操作系统**: Windows 11 / macOS / Linux
- **JDK**: 17.0.x（确保 `java -version` 输出 17）
- **Node.js**: 20.x（确保 `node -v` 输出 v20.x.x）
- **MySQL**: 8.0.x（确保 `mysql --version` 输出 8.0.x）
- **Maven**: 3.9.x（确保 `mvn -version` 输出 3.9.x）

---

## 三、功能模块规划

### 3.1 模块清单

| 模块 | 状态 | 说明 |
|------|------|------|
| 系统管理（用户/角色/菜单） | **本期实现** | 登录认证、RBAC 权限管理 |
| 客户管理 | **本期实现** | 客户信息的 CRUD + 分页查询 |
| 仪表盘 | **本期实现** | 登录后的首页，展示统计数据 |
| 账户管理 | 预留入口 | 仅前端菜单占位，后端接口预留 |
| 交易管理 | 预留入口 | 仅前端菜单占位，后端接口预留 |
| 贷款管理 | 预留入口 | 仅前端菜单占位，后端接口预留 |
| 报表统计 | 预留入口 | 仅前端菜单占位，后端接口预留 |

### 3.2 系统管理模块

- **登录/登出**: 用户名 + 密码登录，JWT 认证
- **用户管理**: 系统用户的 CRUD
- **角色管理**: 角色的 CRUD，关联菜单权限
- **菜单管理**: 菜单树的 CRUD

> 说明：系统管理模块的实现方式是：数据库预置管理员账号和基础菜单数据，用户管理/角色管理/菜单管理以数据库初始化的方式预置，不开发独立的前端管理页面。前端仅关注登录和动态菜单渲染。

### 3.3 客户管理模块

- 客户列表（分页、模糊搜索）
- 新增客户
- 编辑客户
- 查看客户详情
- 删除客户（逻辑删除）

---

## 四、数据库设计（含完整 DDL）

### 4.1 数据库信息

- 数据库名: `bank_system`
- 字符集: `utf8mb4`
- 排序规则: `utf8mb4_unicode_ci`

### 4.2 核心表 DDL

#### 4.2.1 系统用户表 sys_user

```sql
CREATE DATABASE IF NOT EXISTS bank_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bank_system;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)     NOT NULL COMMENT '用户名',
    password    VARCHAR(255)    NOT NULL COMMENT '密码（BCrypt加密）',
    nickname    VARCHAR(50)     DEFAULT NULL COMMENT '昵称',
    email       VARCHAR(100)    DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)     DEFAULT NULL COMMENT '手机号',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-已删除 0-未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

#### 4.2.2 角色表 sys_role

```sql
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name   VARCHAR(50)     NOT NULL COMMENT '角色名称',
    role_code   VARCHAR(50)     NOT NULL COMMENT '角色编码（如 admin、manager、teller）',
    description VARCHAR(200)    DEFAULT NULL COMMENT '角色描述',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

#### 4.2.3 用户角色关联表 sys_user_role

```sql
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

#### 4.2.4 菜单权限表 sys_menu

```sql
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id   BIGINT          NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示一级菜单',
    menu_name   VARCHAR(50)     NOT NULL COMMENT '菜单名称',
    path        VARCHAR(200)    DEFAULT NULL COMMENT '前端路由路径',
    component   VARCHAR(200)    DEFAULT NULL COMMENT '前端组件路径',
    icon        VARCHAR(50)     DEFAULT NULL COMMENT '菜单图标（Element Plus图标名）',
    menu_type   TINYINT         NOT NULL DEFAULT 1 COMMENT '类型：1-目录 2-菜单 3-按钮',
    sort_order  INT             NOT NULL DEFAULT 0 COMMENT '排序号',
    permission  VARCHAR(100)    DEFAULT NULL COMMENT '权限标识（如 customer:add）',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';
```

#### 4.2.5 角色菜单关联表 sys_role_menu

```sql
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (id),
    KEY idx_role_id (role_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
```

#### 4.2.6 客户信息表 customer

```sql
DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(50)     NOT NULL COMMENT '客户姓名',
    id_card     VARCHAR(18)     NOT NULL COMMENT '身份证号',
    phone       VARCHAR(20)     DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100)    DEFAULT NULL COMMENT '邮箱',
    address     VARCHAR(200)    DEFAULT NULL COMMENT '联系地址',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-已删除 0-未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_id_card (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';
```

#### 4.2.7 账户表 account（预留，仅建表）

```sql
DROP TABLE IF EXISTS account;
CREATE TABLE account (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    account_number  VARCHAR(20)     NOT NULL COMMENT '账号',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    account_type    VARCHAR(20)     NOT NULL DEFAULT 'SAVINGS' COMMENT '账户类型：SAVINGS-储蓄 CHECKING-活期',
    balance         DECIMAL(18,2)   NOT NULL DEFAULT 0.00 COMMENT '余额',
    currency        VARCHAR(10)     NOT NULL DEFAULT 'CNY' COMMENT '币种',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常 2-冻结 3-销户',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开户时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_number (account_number),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';
```

#### 4.2.8 交易记录表 transaction_record（预留，仅建表）

```sql
DROP TABLE IF EXISTS transaction_record;
CREATE TABLE transaction_record (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    transaction_no      VARCHAR(32)     NOT NULL COMMENT '交易流水号',
    from_account_id     BIGINT          DEFAULT NULL COMMENT '付款账户ID',
    to_account_id       BIGINT          DEFAULT NULL COMMENT '收款账户ID',
    amount              DECIMAL(18,2)   NOT NULL COMMENT '交易金额',
    transaction_type    VARCHAR(20)     NOT NULL COMMENT '交易类型：DEPOSIT-存款 WITHDRAW-取款 TRANSFER-转账',
    status              TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-成功 2-失败 3-处理中',
    remark              VARCHAR(200)    DEFAULT NULL COMMENT '备注',
    create_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_from_account (from_account_id),
    KEY idx_to_account (to_account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';
```

#### 4.2.9 贷款表 loan（预留，仅建表）

```sql
DROP TABLE IF EXISTS loan;
CREATE TABLE loan (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    loan_no         VARCHAR(32)     NOT NULL COMMENT '贷款编号',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    account_id      BIGINT          NOT NULL COMMENT '关联账户ID',
    principal       DECIMAL(18,2)   NOT NULL COMMENT '贷款本金',
    interest_rate   DECIMAL(6,4)    NOT NULL COMMENT '年利率（如 0.0350 表示 3.5%）',
    total_amount    DECIMAL(18,2)   NOT NULL COMMENT '应还总额',
    remain_amount   DECIMAL(18,2)   NOT NULL COMMENT '剩余未还金额',
    start_date      DATE            NOT NULL COMMENT '贷款开始日期',
    end_date        DATE            NOT NULL COMMENT '贷款到期日期',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常 2-已结清 3-逾期 4-呆账',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_loan_no (loan_no),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贷款表';
```

### 4.3 初始化数据

```sql
-- =============================================
-- 初始化角色
-- =============================================
INSERT INTO sys_role (id, role_name, role_code, description) VALUES
(1, '超级管理员', 'admin',    '拥有所有权限'),
(2, '客户经理',   'manager',  '可管理客户信息'),
(3, '柜员',       'teller',   '可查看客户信息');

-- =============================================
-- 初始化管理员用户（密码: admin123，BCrypt加密）
-- =============================================
-- BCrypt 加密后的 admin123:
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO sys_user (id, username, password, nickname, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1);

-- 管理员关联 admin 角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- =============================================
-- 初始化菜单
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, menu_type, sort_order, permission) VALUES
-- 一级目录
(1,  0, '仪表盘',   '/dashboard',     'dashboard/Index.vue',   'HomeFilled',     2, 1, NULL),
(2,  0, '客户管理', NULL,             NULL,                    'User',            1, 2, NULL),
(3,  0, '账户管理', NULL,             NULL,                    'CreditCard',      1, 3, NULL),
(4,  0, '交易管理', NULL,             NULL,                    'Money',           1, 4, NULL),
(5,  0, '贷款管理', NULL,             NULL,                    'BankCard',        1, 5, NULL),
(6,  0, '报表统计', NULL,             NULL,                    'DataAnalysis',    1, 6, NULL),
(7,  0, '系统管理', NULL,             NULL,                    'Setting',         1, 7, NULL),
-- 客户管理子菜单
(21, 2, '客户列表', '/customer/list', 'customer/List.vue',     'List',            2, 1, 'customer:list'),
(22, 2, '新增客户', '/customer/add',  'customer/Form.vue',     'Plus',            2, 2, 'customer:add'),
-- 系统管理子菜单
(71, 7, '用户管理', '/system/user',   'system/UserList.vue',   'UserFilled',      2, 1, 'system:user'),
(72, 7, '角色管理', '/system/role',   'system/RoleList.vue',   'Avatar',          2, 2, 'system:role'),
(73, 7, '菜单管理', '/system/menu',   'system/MenuList.vue',   'Menu',            2, 3, 'system:menu');

-- 为 admin 角色分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;
```

---

## 五、Spring Security + JWT 认证方案

### 5.1 认证流程

```
1. 用户输入用户名密码 → POST /api/auth/login
2. 后端验证用户名密码 → 生成 JWT Token
3. 前端将 Token 存入 localStorage
4. 后续请求在 Header 中携带: Authorization: Bearer <token>
5. 后端 JWT 过滤器解析 Token → 设置 SecurityContext
6. Token 过期后前端跳转登录页
```

### 5.2 登录接口

**请求:**

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer ",
    "expiresIn": 86400
  }
}
```

### 5.3 获取当前用户信息

**请求:**

```
GET /api/auth/info
Authorization: Bearer <token>
```

**响应:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "roles": ["admin"],
    "menus": [
      {
        "id": 1,
        "parentId": 0,
        "menuName": "仪表盘",
        "path": "/dashboard",
        "component": "dashboard/Index.vue",
        "icon": "HomeFilled",
        "children": []
      },
      {
        "id": 2,
        "parentId": 0,
        "menuName": "客户管理",
        "path": null,
        "icon": "User",
        "children": [
          {
            "id": 21,
            "parentId": 2,
            "menuName": "客户列表",
            "path": "/customer/list",
            "component": "customer/List.vue",
            "icon": "List"
          }
        ]
      }
    ]
  }
}
```

### 5.4 JWT 配置参数

| 参数 | 值 | 说明 |
|------|-----|------|
| secret | `bank-system-jwt-secret-key-2026` | JWT 签名密钥 |
| expiration | 86400 秒（24 小时） | Token 有效期 |
| header | `Authorization` | 请求头名称 |
| tokenHead | `Bearer ` | Token 前缀 |

### 5.5 RBAC 权限模型

- **用户 ↔ 角色**: 多对多（sys_user_role）
- **角色 ↔ 菜单**: 多对多（sys_role_menu）
- **权限判断**: 前端根据菜单列表动态渲染路由；后端通过 `@PreAuthorize` 注解做接口级权限控制

### 5.6 前端 Token 管理

- 登录成功后存储 `token` 到 `localStorage`
- Axios 请求拦截器：自动在 `Authorization` header 添加 `Bearer <token>`
- Axios 响应拦截器：收到 401 状态码时清除 token 并跳转 `/login`
- Vue Router 全局前置守卫：未登录用户只能访问 `/login`，其他页面重定向到登录页

---

## 六、统一响应格式

### 6.1 普通响应

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 6.2 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 6.3 错误响应

```json
{
  "code": 500,
  "message": "系统内部错误",
  "data": null
}
```

### 6.4 后端统一响应类（Java）

在后端定义一个 `Result<T>` 类：

```java
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> success(String message, T data) { ... }
    public static <T> Result<T> error(int code, String message) { ... }
    public static <T> Result<T> error(ResultCode resultCode) { ... }
}
```

### 6.5 错误码枚举

```java
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),
    INTERNAL_ERROR(500, "系统内部错误");

    private final int code;
    private final String message;
}
```

### 6.6 分页请求参数规范

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 当前页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| keyword | String | 否 | 模糊搜索关键字 |
| 其他... | - | 否 | 按具体接口定义 |

---

## 七、客户管理模块详细设计

### 7.1 API 接口列表

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/customers` | 分页查询客户列表 | `customer:list` |
| GET | `/api/customers/{id}` | 获取客户详情 | `customer:list` |
| POST | `/api/customers` | 新增客户 | `customer:add` |
| PUT | `/api/customers/{id}` | 更新客户信息 | `customer:add` |
| DELETE | `/api/customers/{id}` | 删除客户（逻辑删除） | `customer:add` |

### 7.2 接口详细定义

#### 7.2.1 分页查询客户列表

```
GET /api/customers?page=1&size=10&keyword=张三
Authorization: Bearer <token>
```

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否，默认 1 | 当前页码 |
| size | int | 否，默认 10 | 每页条数 |
| keyword | String | 否 | 按姓名、手机号、身份证号模糊搜索 |

**响应:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "idCard": "110101199001011234",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "address": "北京市朝阳区xxx路100号",
        "status": 1,
        "createTime": "2026-05-01 10:30:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

#### 7.2.2 获取客户详情

```
GET /api/customers/{id}
Authorization: Bearer <token>
```

**响应:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "idCard": "110101199001011234",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "address": "北京市朝阳区xxx路100号",
    "status": 1,
    "createTime": "2026-05-01 10:30:00",
    "updateTime": "2026-05-10 14:20:00"
  }
}
```

#### 7.2.3 新增客户

```
POST /api/customers
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "张三",
  "idCard": "110101199001011234",
  "phone": "13800138000",
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区xxx路100号"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "新增成功",
  "data": null
}
```

#### 7.2.4 更新客户

```
PUT /api/customers/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": 1,
  "name": "张三（已更新）",
  "idCard": "110101199001011234",
  "phone": "13800138001",
  "email": "zhangsan_new@example.com",
  "address": "北京市海淀区xxx路200号"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

#### 7.2.5 删除客户

```
DELETE /api/customers/{id}
Authorization: Bearer <token>
```

**响应:**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 7.3 业务规则与校验

| 规则 | 校验时机 | 说明 |
|------|---------|------|
| 姓名必填，长度 2-50 字符 | 新增/更新 | `@NotBlank` `@Size(min=2, max=50)` |
| 身份证号必填，18 位 | 新增/更新 | `@NotBlank` `@Size(min=18, max=18)` |
| 身份证号唯一 | 新增/更新 | 数据库唯一约束 + 业务层校验 |
| 手机号格式校验 | 新增/更新 | 正则 `^1[3-9]\d{9}$` |
| 邮箱格式校验 | 新增/更新 | `@Email` 注解 |
| 身份证号不可修改 | 仅更新时 | 编辑表单中身份证号置灰 |
| 逻辑删除 | 删除 | 设置 deleted=1，不清除数据 |

### 7.4 后端 DTO 定义

#### 7.4.1 CustomerCreateDTO（新增请求体）

```java
public class CustomerCreateDTO {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度为2-50个字符")
    private String name;

    @NotBlank(message = "身份证号不能为空")
    @Size(min = 18, max = 18, message = "身份证号必须为18位")
    private String idCard;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
}
```

#### 7.4.2 CustomerUpdateDTO（更新请求体）

```java
public class CustomerUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    // 其余字段同 CustomerCreateDTO
}
```

#### 7.4.3 CustomerQueryDTO（查询请求参数）

```java
public class CustomerQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;  // 模糊搜索：姓名、手机号、身份证号
}
```

#### 7.4.4 CustomerVO（响应对象）

```java
public class CustomerVO {
    private Long id;
    private String name;
    private String idCard;
    private String phone;
    private String email;
    private String address;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 7.5 前端页面设计

#### 7.5.1 客户列表页 `customer/List.vue`

**页面结构:**
```
┌─────────────────────────────────────────────────┐
│  搜索区域                                        │
│  ┌─────────────────────────┬─────────────┐      │
│  │ 关键字搜索框（姓名/手机/身份证）  │ [查询] [重置] │      │
│  └─────────────────────────┴─────────────┘      │
├─────────────────────────────────────────────────┤
│  操作栏                                         │
│  [+ 新增客户]                      [导出]        │
├─────────────────────────────────────────────────┤
│  数据表格（Element Plus el-table）              │
│  ID | 姓名 | 身份证号 | 手机号 | 状态 | 创建时间 | 操作 │
│  ───────────────────────────────────────────── │
│  1  | 张三 | 1101xxxx | 138xxx | 正常 | 05-01  | 详情/编辑/删除│
│  2  | 李四 | 1102xxxx | 139xxx | 正常 | 05-02  | 详情/编辑/删除│
├─────────────────────────────────────────────────┤
│  分页组件（el-pagination）                      │
└─────────────────────────────────────────────────┘
```

**交互说明:**
- 进入页面自动查询第 1 页
- 点击"查询"按关键字搜索
- 点击"重置"清空搜索条件并重新查询
- 点击"新增客户"跳转到 `/customer/add`
- 点击"详情"跳转到 `/customer/:id` 详情页
- 点击"编辑"弹窗编辑表单（或跳转到编辑页）
- 点击"删除"弹出二次确认（el-message-box），确认后调用删除接口

#### 7.5.2 客户表单页 `customer/Form.vue`

**页面结构:**
```
┌─────────────────────────────────────────────────┐
│  < 返回列表   新增客户 / 编辑客户              │
├─────────────────────────────────────────────────┤
│  表单（el-form）                                │
│  ┌─────────────────────────────────────┐       │
│  │ 姓名:      [____________]           │       │
│  │ 身份证号:  [____________] (编辑时禁用)│       │
│  │ 手机号:    [____________]           │       │
│  │ 邮箱:      [____________]           │       │
│  │ 地址:      [____________]           │       │
│  └─────────────────────────────────────┘       │
│                                                 │
│  [保存] [取消]                                  │
└─────────────────────────────────────────────────┘
```

**交互说明:**
- 新增模式：标题显示"新增客户"，所有字段可编辑
- 编辑模式：标题显示"编辑客户"，身份证号字段置灰不可修改
- 点击"保存"先进行表单校验，校验通过后调用接口
- 保存成功后 redirect 到列表页
- 点击"取消"返回列表页

#### 7.5.3 客户详情页 `customer/Detail.vue`

**页面结构:**
```
┌─────────────────────────────────────────────────┐
│  < 返回列表   客户详情                          │
├─────────────────────────────────────────────────┤
│  描述列表（el-descriptions）                    │
│  姓名:    张三                                  │
│  身份证号: 110101199001011234                   │
│  手机号:   13800138000                          │
│  邮箱:     zhangsan@example.com                 │
│  地址:     北京市朝阳区xxx路100号               │
│  状态:     正常                                  │
│  创建时间: 2026-05-01 10:30:00                 │
│  更新时间: 2026-05-10 14:20:00                 │
│                                                 │
│  [编辑]  [返回列表]                             │
└─────────────────────────────────────────────────┘
```

---

## 八、前端路由与布局设计

### 8.1 路由表

| 路由路径 | 组件 | 名称 | 元信息 | 说明 |
|----------|------|------|--------|------|
| `/login` | `login/Index.vue` | Login | `{ noAuth: true }` | 登录页，不校验 token |
| `/` | `layout/Index.vue` | Layout | - | 后台布局容器，所有子路由需认证 |
| `/dashboard` | `dashboard/Index.vue` | Dashboard | `{ title: '仪表盘', icon: 'HomeFilled' }` | 首页 |
| `/customer/list` | `customer/List.vue` | CustomerList | `{ title: '客户列表', icon: 'List' }` | 客户列表 |
| `/customer/add` | `customer/Form.vue` | CustomerAdd | `{ title: '新增客户', hidden: true }` | 新增客户（不在菜单显示） |
| `/customer/:id` | `customer/Detail.vue` | CustomerDetail | `{ title: '客户详情', hidden: true }` | 客户详情（不在菜单显示） |
| `/customer/:id/edit` | `customer/Form.vue` | CustomerEdit | `{ title: '编辑客户', hidden: true }` | 编辑客户（不在菜单显示） |
| `/system/user` | `system/UserList.vue` | SystemUser | `{ title: '用户管理', icon: 'UserFilled' }` | 用户管理 |
| `/system/role` | `system/RoleList.vue` | SystemRole | `{ title: '角色管理', icon: 'Avatar' }` | 角色管理 |
| `/system/menu` | `system/MenuList.vue` | SystemMenu | `{ title: '菜单管理', icon: 'Menu' }` | 菜单管理 |

### 8.2 路由实现方式

Vue Router 配置：

- 静态路由（写死）:
  - `/login` — 登录页
  - `/` → Layout — 后台布局容器，包含 `/dashboard`

- 动态路由（登录后根据后端返回的菜单列表 addRoute）:
  - 客户管理相关路由
  - 系统管理相关路由
  - 预留模块路由（账户/交易/贷款/报表 — 仅占位，无实际页面时跳转到提示页）

### 8.3 后台管理布局 `layout/Index.vue`

```
┌──────────────────────────────────────────────────────┐
│  Header (顶部导航栏，高度 60px，el-header)             │
│  ┌─────────────────────────────────────────────────┐ │
│  │  🏦 Simple Bank System     [用户头像 ▼] [退出]  │ │
│  └─────────────────────────────────────────────────┘ │
├────────┬─────────────────────────────────────────────┤
│ Sidebar│ Main Content (el-main)                      │
│ (250px)│                                              │
│        │  <router-view />                             │
│ 菜单1  │                                              │
│  子菜单│                                              │
│ 菜单2  │                                              │
│  子菜单│                                              │
│ 菜单3  │                                              │
│        │                                              │
├────────┴─────────────────────────────────────────────┤
```

**布局组件说明:**
- 使用 Element Plus 的 `el-container`、`el-header`、`el-aside`、`el-main` 组件
- 侧边栏菜单使用 `el-menu`，模式为 `vertical`，用 `router` 属性启用路由跳转
- 菜单根据 `/api/auth/info` 返回的 menus 树动态渲染
- 顶部右侧展示当前用户昵称和退出按钮
- 退出时清除 localStorage 中的 token 并跳转 `/login`

---

## 九、项目结构与包结构

### 9.1 整体项目目录

```
bank-system/
├── backend/                        # 后端 Spring Boot 项目
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/bank/system/
│       │   │   ├── BankSystemApplication.java
│       │   │   ├── config/         # 配置类
│       │   │   ├── controller/     # 控制器
│       │   │   ├── service/        # 业务接口
│       │   │   │   └── impl/       # 业务实现
│       │   │   ├── mapper/         # MyBatis-Plus Mapper
│       │   │   ├── entity/         # 数据库实体
│       │   │   ├── dto/            # 请求/响应 DTO
│       │   │   ├── vo/             # 视图对象
│       │   │   ├── common/         # 通用类（Result、异常等）
│       │   │   └── security/       # Spring Security + JWT 相关
│       │   └── resources/
│       │       ├── application.yml
│       │       └── sql/            # 数据库初始化 SQL
│       │           └── init.sql
│       └── test/
├── frontend/                       # 前端 Vue 项目
│   ├── package.json
│   ├── vite.config.ts
│   ├── index.html
│   ├── .env.development
│   ├── .env.production
│   └── src/
│       ├── main.ts                 # 入口文件
│       ├── App.vue                 # 根组件
│       ├── router/
│       │   └── index.ts            # 路由配置
│       ├── stores/
│       │   ├── auth.ts             # 认证状态（token、用户信息）
│       │   ├── app.ts              # 应用状态（侧边栏折叠等）
│       │   └── tabs.ts             # 标签页状态（Tab 导航管理）
│       ├── api/
│       │   ├── request.ts          # Axios 封装（拦截器）
│       │   ├── auth.ts             # 认证相关 API
│       │   └── customer.ts         # 客户相关 API
│       ├── styles/
│       │   ├── theme.css           # CSS 变量主题系统（霓虹暗黑风）
│       │   └── global.css          # 全局样式、动画、工具类
│       ├── views/
│       │   ├── login/
│       │   │   └── Index.vue
│       │   ├── layout/
│       │   │   ├── Index.vue       # 后台布局（侧边栏 + 头部 + 标签栏 + 内容）
│       │   │   ├── TabBar.vue      # 标签页栏组件
│       │   │   └── Breadcrumb.vue  # 面包屑组件
│       │   ├── dashboard/
│       │   │   └── Index.vue
│       │   ├── customer/
│       │   │   ├── List.vue
│       │   │   ├── Form.vue
│       │   │   └── Detail.vue
│       │   └── system/
│       │       ├── UserList.vue
│       │       ├── RoleList.vue
│       │       └── MenuList.vue
│       └── utils/
│           └── auth.ts             # Token 读写工具函数
└── docs/
    └── requirements.md             # 本文档
```

### 9.2 后端包详细说明

| 包路径 | 说明 | 主要内容 |
|--------|------|---------|
| `config/` | 配置类 | `SecurityConfig`、`MyBatisPlusConfig`、`CorsConfig` |
| `controller/` | REST 控制器 | `AuthController`、`CustomerController` |
| `service/` | 业务接口 | `CustomerService`、`UserService` |
| `service/impl/` | 业务实现 | `CustomerServiceImpl`、`UserServiceImpl` |
| `mapper/` | MyBatis-Plus Mapper | `CustomerMapper`、`UserMapper`、`RoleMapper`、`MenuMapper` |
| `entity/` | 数据库实体 | `Customer`、`SysUser`、`SysRole`、`SysMenu`（使用 `@TableLogic` 逻辑删除） |
| `dto/` | 数据传输对象 | `CustomerCreateDTO`、`CustomerUpdateDTO`、`CustomerQueryDTO`、`LoginDTO` |
| `vo/` | 视图对象 | `CustomerVO`、`UserInfoVO`、`MenuVO` |
| `common/` | 通用类 | `Result<T>`、`ResultCode`、`GlobalExceptionHandler`、`BusinessException` |
| `security/` | 安全相关 | `JwtTokenProvider`、`JwtAuthenticationFilter`、`UserDetailsServiceImpl` |

### 9.3 前端目录详细说明

| 路径 | 说明 |
|------|------|
| `api/request.ts` | Axios 实例，baseURL 配置，请求拦截器（加 token），响应拦截器（401 处理） |
| `api/auth.ts` | `login()`、`getUserInfo()` |
| `api/customer.ts` | `getCustomerList()`、`getCustomerById()`、`createCustomer()`、`updateCustomer()`、`deleteCustomer()` |
| `stores/auth.ts` | Pinia store，管理 token、userInfo、menus，提供 `doLogin()`、`fetchUserInfo()`、`doLogout()` action |
| `stores/app.ts` | Pinia store，管理 sidebarCollapsed 等全局状态 |
| `stores/tabs.ts` | Pinia store，管理标签页（tabs[]、activeTab），提供 `openTab()`、`closeTab()`、`setActiveTab()`、`syncFromRoute()` 等 action，计算 `cachedViewNames` 供 keep-alive 使用 |
| `styles/theme.css` | CSS 自定义属性体系（暗黑背景、霓虹色板、玻璃效果、辉光阴影），Element Plus 80+ 变量深度覆盖 |
| `styles/global.css` | 全局重置、滚动条、星空背景、扫描线叠加、动画关键帧、工具类（.glass-card / .text-gradient / .pulse-border）、Element Plus 组件深层定制 |
| `router/index.ts` | Vue Router 配置，所有页面路由嵌套在 Layout 下，`beforeEach` 鉴权守卫，`afterEach` 标签页同步 |
| `utils/auth.ts` | `getToken()`、`setToken()`、`removeToken()` 工具函数 |
| `views/layout/Index.vue` | 后台布局：el-container + el-aside（霓虹侧边栏 + 动态菜单 @select）+ el-header（面包屑 + 用户头像）+ TabBar + el-main（keep-alive router-view） |
| `views/layout/TabBar.vue` | 标签页栏：横向可滚动 pill 标签，激活态霓虹底边，右键菜单（关闭/关闭其他/关闭左右侧/关闭所有），中键关闭，鼠标滚轮横向滚动 |
| `views/layout/Breadcrumb.vue` | 面包屑导航：从 `route.matched` 自动生成，末级高亮，点击跳转 |

---

## 十、关键配置文件

### 10.1 后端 `application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bank_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: bank-system-jwt-secret-key-2026
  expiration: 86400
  header: Authorization
  token-head: "Bearer "
```

### 10.2 前端 `.env.development`

```
VITE_API_BASE_URL=http://localhost:8080
```

### 10.3 前端 `.env.production`

```
VITE_API_BASE_URL=http://localhost:8080
```

### 10.4 后端 `pom.xml` 关键依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>

<properties>
    <java.version>17</java.version>
    <mybatis-plus.version>3.5.6</mybatis-plus.version>
    <jjwt.version>0.12.5</jjwt.version>
</properties>

<!-- 关键依赖 -->
<!-- spring-boot-starter-web -->
<!-- spring-boot-starter-security -->
<!-- spring-boot-starter-validation -->
<!-- mysql-connector-j -->
<!-- mybatis-plus-boot-starter -->
<!-- lombok -->
<!-- jjwt-api / jjwt-impl / jjwt-jackson -->
```

---

## 十一、异常处理与通用组件

### 11.1 全局异常处理器 `GlobalExceptionHandler`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    // Spring Security 权限异常
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(ResultCode.FORBIDDEN);
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常:", e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}
```

### 11.2 业务异常类 `BusinessException`

```java
public class BusinessException extends RuntimeException {
    private int code;
    private String message;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
}
```

### 11.3 Spring Security 配置要点

`SecurityConfig` 需要实现：

1. 放行路径: `/api/auth/login`、错误页面
2. JWT 过滤器: 在 `UsernamePasswordAuthenticationFilter` 之前插入 `JwtAuthenticationFilter`
3. 禁用 Session（无状态）
4. 配置 CORS（允许前端 5173 端口跨域）
5. 配置 `@EnableMethodSecurity` 以支持 `@PreAuthorize` 注解

### 11.4 前端 Axios 封装 `api/request.ts`

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { getToken, removeToken } from '@/utils/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000
})

// 请求拦截器：自动加 Authorization header
request.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理 401
request.interceptors.response.use(
  response => {
    const { code, message } = response.data
    if (code !== 200) {
      ElMessage.error(message || '操作失败')
      return Promise.reject(new Error(message))
    }
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      removeToken()
      router.push('/login')
    }
    ElMessage.error('网络请求失败')
    return Promise.reject(error)
  }
)

export default request
```

---

## 十二、开发与部署说明

### 12.1 数据库初始化

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行初始化脚本
source /path/to/bank-system/backend/src/main/resources/sql/init.sql
```

init.sql 包含：
- 建库语句
- 所有表的 DDL
- 初始化数据（admin 用户、角色、菜单、角色-菜单关联）

### 12.2 后端启动

```bash
cd bank-system/backend

# 安装依赖
mvn clean install -DskipTests

# 启动
mvn spring-boot:run

# 验证：访问 http://localhost:8080，看到 401 或空白页即启动成功
```

### 12.3 前端启动

```bash
cd bank-system/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 验证：浏览器访问 http://localhost:5173，自动跳转登录页
```

### 12.4 测试登录

1. 浏览器打开 `http://localhost:5173`
2. 自动跳转到登录页
3. 输入：
   - 用户名: `admin`
   - 密码: `admin123`
4. 登录成功后进入仪表盘页面
5. 左侧菜单可见"客户管理"、"系统管理"等菜单项

### 12.5 测试客户管理功能

1. 点击"客户管理" → "客户列表"
2. 点击"新增客户"，填写表单
3. 提交后在列表中看到新客户
4. 点击"详情"查看客户完整信息
5. 点击"编辑"修改客户信息
6. 点击"删除"确认后删除客户

### 12.6 关键点提示

1. **CORS 跨域**: 后端需要配置允许 `http://localhost:5173` 跨域访问
2. **端口冲突**: 确保 8080、5173、3306 端口未被占用
3. **MySQL 密码**: 根据实际环境修改 `application.yml` 中的数据库密码
4. **逻辑删除**: MyBatis-Plus 配置了 `@TableLogic`，删除操作自动转为 UPDATE
5. **密码加密**: 新增用户时密码需使用 BCryptPasswordEncoder 加密后存储

---

## 十三、前端 UI 升级（科技风 + 标签页导航）

> 更新时间：2026 年 5 月 23 日

### 13.1 升级概述

在基础功能完成后，对前端进行了全面的 UI/UX 升级，主要包括：

1. **暗黑科技风视觉主题**：霓虹发光 + 玻璃拟态 + 星空背景 + CRT 扫描线
2. **标签页导航系统**：侧边栏点击打开新标签页，而非替换当前页面
3. **用户交互优化**：面包屑、keep-alive 缓存、右键菜单、中键关闭等

### 13.2 视觉主题设计

#### 13.2.1 CSS 变量体系

所有颜色、阴影、圆角通过 CSS 自定义属性集中管理（`src/styles/theme.css`），作用域为 `html.dark`：

| 类别 | 变量 | 说明 |
|------|------|------|
| 背景 | `--bg-primary: #060918` | 深空黑（页面底色） |
| 背景 | `--bg-secondary: #0d1230` | 深海军蓝（侧边栏/卡片） |
| 背景 | `--bg-glass: rgba(13,18,48,0.65)` | 玻璃面板半透明背景 |
| 霓虹色 | `--neon-cyan: #00d4ff` | 主交互色（按钮/高亮/辉光） |
| 霓虹色 | `--neon-purple: #a855f7` | 渐变辅助色 |
| 霓虹色 | `--neon-green: #22c55e` | 成功状态 |
| 霓虹色 | `--neon-red: #ef4444` | 危险操作 |
| 文字 | `--text-primary: #e8eaed` | 主要文本 |
| 文字 | `--text-secondary: #9aa0a6` | 次要文本 |
| 效果 | `--glass-blur: 12px` | 玻璃模糊度 |
| 效果 | `--glow-cyan` | 青色辉光阴影 |

#### 13.2.2 Element Plus 深度覆盖

覆盖 80+ 个 Element Plus 暗黑变量（`--el-bg-color`、`--el-color-primary`、`--el-table-bg-color` 等），使所有 Element Plus 组件（表格、按钮、输入框、菜单、对话框、分页等）自动匹配科技风主题。

#### 13.2.3 视觉特效

| 效果 | 实现方式 | 说明 |
|------|---------|------|
| 星空背景 | CSS `radial-gradient` 多点 + `animation: starDrift` | body 层 13 个星点缓慢漂移 |
| 扫描线 | `::after` 伪元素 + `repeating-linear-gradient` | 贯穿全屏的 CRT 风格水平线 |
| 玻璃卡片 | `.glass-card` 工具类 | `backdrop-filter: blur()` + 半透明边框 + 悬浮发光 |
| 霓虹辉光 | `box-shadow` 变量 + `.pulse-border` 动画 | 边框呼吸发光（3s 周期） |
| 渐变文字 | `.text-gradient` 工具类 | cyan→purple 渐变 + `text-shadow` 发光 |
| 自定义滚动条 | `::-webkit-scrollbar` | 6px 宽、霓虹拇指、暗色轨道 |

### 13.3 标签页导航系统

#### 13.3.1 数据模型

标签页由 `stores/tabs.ts`（Pinia Store）管理：

```typescript
interface TabItem {
  path: string        // 路由路径，唯一标识（如 '/customer/list'）
  title: string       // 标签显示名称
  icon?: string       // Element Plus 图标名
  affix?: boolean     // 是否固定（不可关闭），仪表盘为 true
}
```

- `tabs: TabItem[]` — 所有打开的标签页
- `activeTab: string` — 当前激活标签的 path

#### 13.3.2 交互行为

| 操作 | 行为 |
|------|------|
| 点击侧边栏菜单 | 若标签页不存在 → 新建并激活；若已存在 → 直接切换 |
| 点击标签页 | 切换到该标签页，URL 同步更新 |
| 点击标签页 × 按钮 | 关闭标签页（affix 标签无 × 按钮） |
| 鼠标中键点击标签页 | 关闭该标签页 |
| 鼠标滚轮在标签栏 | 横向滚动（标签页超出屏幕宽度时） |
| 右键标签页 | 弹出菜单：关闭 / 关闭其他 / 关闭左侧 / 关闭右侧 / 关闭所有 |
| 直接输入 URL | `router.afterEach` 自动创建对应标签页 |
| 关闭当前激活标签页 | 自动切换到相邻标签页 |

#### 13.3.3 组件缓存

使用 `<keep-alive :include="cachedViewNames">` 包裹 `<router-view>`，切换标签页时保持组件状态不丢失（表单填写内容、列表滚动位置等）。每个页面组件通过 `<script lang="ts">export default { name: 'Xxx' }</script>` 声明组件名供 keep-alive 识别。

#### 13.3.4 路由结构调整

所有页面路由嵌套在 Layout（`/`）下作为 children：

```
/ → Layout (容器)
  ├── /dashboard       → 仪表盘（affix 固定标签）
  ├── /customer/list   → 客户列表
  ├── /customer/add    → 新增客户
  ├── /customer/:id    → 客户详情（hidden，不在侧边栏显示）
  ├── /customer/:id/edit → 编辑客户（hidden）
  ├── /system/user     → 用户管理
  ├── /system/role     → 角色管理
  └── /system/menu     → 菜单管理
```

### 13.4 新增/修改文件清单

#### 新增文件

| 文件 | 说明 |
|------|------|
| `src/styles/theme.css` | CSS 变量定义 + Element Plus 暗黑覆盖 |
| `src/styles/global.css` | 全局样式、动画、工具类、星空背景 |
| `src/stores/tabs.ts` | 标签页状态管理 Store |
| `src/views/layout/TabBar.vue` | 标签页栏组件 |
| `src/views/layout/Breadcrumb.vue` | 面包屑导航组件 |

#### 修改文件

| 文件 | 改动内容 |
|------|---------|
| `index.html` | `<html>` 添加 `class="dark"`，标题改为 NovaBank |
| `src/main.ts` | 导入 `theme.css` 和 `global.css` |
| `src/App.vue` | 添加背景色 + 路由切换过渡动画 |
| `src/router/index.ts` | 所有路由嵌套到 Layout children；添加 `afterEach` 标签页同步钩子；路由 meta 增加 affix/hidden 字段 |
| `src/views/layout/Index.vue` | 移除 `el-menu :router`，改用 `@select` + tabStore；集成 Breadcrumb + TabBar；侧边栏/头部/内容区全面改版为科技风样式 |
| `src/views/login/Index.vue` | 星空粒子背景 + 扫描线叠加 + 玻璃卡片 + 渐变标题 + 霓虹按钮 |
| `src/views/dashboard/Index.vue` | 玻璃统计卡片（悬浮上浮 + 渐变数值）+ 快捷操作面板 |
| `src/views/customer/List.vue` | 玻璃搜索栏 + 暗黑表格（霓虹表头）+ 操作改为链接按钮 + 详情/编辑打开新标签 |
| `src/views/customer/Form.vue` | 玻璃表单卡片 + 保存/取消后关闭当前标签并切回列表 |
| `src/views/customer/Detail.vue` | 玻璃描述列表 + 编辑打开新标签 + 返回关闭当前标签 |
| `src/views/system/*.vue` | 统一改为玻璃卡片 + 返回首页关闭当前标签 |
| `src/stores/auth.ts` | MenuItem 接口增加 `menuType` 字段 |

### 13.5 验证要点

1. 登录页应有星空背景 + 扫描线效果 + 玻璃质感卡片
2. 登录后仪表盘显示 4 张发光统计卡片 + 快捷操作区
3. 侧边栏菜单为暗黑 + 霓虹主题，点击客户列表打开新标签页
4. 标签栏可左右滚动（滚轮），激活标签有霓虹底边
5. 右键标签页弹出完整菜单，中键可关闭
6. 客户表单编辑后保存 → 自动关闭标签并跳转列表
7. 客户详情点击编辑 → 打开新标签页（不关闭详情页）
8. 标签页切换后表单内容/滚动位置保持（keep-alive 生效）
9. 所有 Element Plus 组件（表格/按钮/弹窗/分页）自动匹配暗黑主题
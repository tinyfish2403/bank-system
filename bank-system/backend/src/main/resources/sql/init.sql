CREATE DATABASE IF NOT EXISTS bank_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bank_system;

DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS contract;
DROP TABLE IF EXISTS credit_approval;
DROP TABLE IF EXISTS credit_application;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS transaction_record;
DROP TABLE IF EXISTS loan;

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

CREATE TABLE sys_user_role (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

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

CREATE TABLE sys_role_menu (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (id),
    KEY idx_role_id (role_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

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

CREATE TABLE credit_application (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    application_no  VARCHAR(32)     NOT NULL COMMENT '申请编号',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    credit_amount   DECIMAL(18,2)   NOT NULL COMMENT '申请授信金额',
    credit_type     VARCHAR(20)     NOT NULL COMMENT '授信类型：WORKING-流动资金 LOAN-贷款 GUARANTEE-担保',
    purpose         VARCHAR(500)    DEFAULT NULL COMMENT '申请用途',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-待审批 2-已批复 3-已拒绝',
    apply_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-已删除 0-未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_application_no (application_no),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信申请表';

CREATE TABLE credit_approval (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    approval_no     VARCHAR(32)     NOT NULL COMMENT '批复编号',
    application_id  BIGINT          NOT NULL COMMENT '授信申请ID',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    approved_amount DECIMAL(18,2)   NOT NULL COMMENT '批复金额',
    credit_limit    DECIMAL(18,2)   NOT NULL COMMENT '授信额度',
    start_date      DATE            NOT NULL COMMENT '有效期开始',
    end_date        DATE            NOT NULL COMMENT '有效期结束',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-有效 2-已过期 3-已撤销',
    approval_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '批复时间',
    remark          VARCHAR(500)    DEFAULT NULL COMMENT '批复意见',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-已删除 0-未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_approval_no (approval_no),
    KEY idx_application_id (application_id),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信批复表';

CREATE TABLE contract (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    contract_no     VARCHAR(32)     NOT NULL COMMENT '合同编号',
    approval_id     BIGINT          NOT NULL COMMENT '授信批复ID',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    contract_amount DECIMAL(18,2)   NOT NULL COMMENT '合同金额',
    contract_type   VARCHAR(20)     NOT NULL COMMENT '合同类型：CREDIT-授信合同 LOAN-贷款合同 GUARANTEE-担保合同',
    sign_date       DATE            NOT NULL COMMENT '签订日期',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-有效 2-已变更 3-已作废',
    remark          VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-已删除 0-未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contract_no (contract_no),
    KEY idx_approval_id (approval_id),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- =============================================
-- 初始化角色
-- =============================================
INSERT INTO sys_role (id, role_name, role_code, description) VALUES
(1, '超级管理员', 'admin',    '拥有所有权限'),
(2, '客户经理',   'manager',  '可管理客户信息'),
(3, '柜员',       'teller',   '可查看客户信息');

-- =============================================
-- 初始化管理员用户（密码: 123456）
-- =============================================
INSERT INTO sys_user (id, username, password, nickname, status) VALUES
(1, 'admin', '$2a$12$POk9IT06zL5AnP8RWOT/ouRjjoEixmx3EEmoQ8uYtqiMMHTnBUt7u', '系统管理员', 1);

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
(8,  0, '授信管理', NULL,             NULL,                    'FolderOpened',    1, 8, NULL),
(9,  0, '合同管理', NULL,             NULL,                    'DocumentCopy',    1, 9, NULL),
-- 客户管理子菜单
(21, 2, '客户列表', '/customer/list', 'customer/List.vue',     'List',            2, 1, 'customer:list'),
(22, 2, '新增客户', '/customer/add',  'customer/Form.vue',     'Plus',            2, 2, 'customer:add'),
-- 系统管理子菜单
(71, 7, '用户管理', '/system/user',   'system/UserList.vue',   'UserFilled',      2, 1, 'system:user'),
(72, 7, '角色管理', '/system/role',   'system/RoleList.vue',   'Avatar',          2, 2, 'system:role'),
(73, 7, '菜单管理', '/system/menu',   'system/MenuList.vue',   'Menu',            2, 3, 'system:menu'),
-- 授信管理子菜单
(81, 8, '授信申请', '/credit/application/list', 'credit/ApplicationList.vue', 'Document', 2, 1, 'credit:application'),
(82, 8, '批复查询', '/credit/approval/list',    'credit/ApprovalList.vue',    'Checked',   2, 2, 'credit:approval'),
-- 合同管理子菜单
(91, 9, '合同列表', '/contract/list', 'contract/List.vue',     'Tickets',         2, 1, 'contract:list'),
(92, 9, '合同签订', '/contract/add',  'contract/Form.vue',     'EditPen',         2, 2, 'contract:add');

-- 为 admin 角色分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;
CREATE DATABASE IF NOT EXISTS cloud_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cloud_demo;

DROP TABLE IF EXISTS biz_order;

CREATE TABLE biz_order (
    order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_no VARCHAR(64) NOT NULL COMMENT '业务单号',
    buyer_user_id BIGINT NOT NULL COMMENT '购买人',
    product_code VARCHAR(64) NOT NULL COMMENT '商品编码',
    quantity INT NOT NULL COMMENT '数量',
    total_amount DECIMAL(18,2) NOT NULL COMMENT '总金额',
    status INT NOT NULL DEFAULT 10 COMMENT '状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_buyer (buyer_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 管理端订单表（与 biz_order 独立，供后台 CRUD 展示）
CREATE TABLE IF NOT EXISTS biz_admin_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_no VARCHAR(64) NOT NULL COMMENT '业务单号',
    buyer_name VARCHAR(64) NOT NULL COMMENT '买家',
    product_code VARCHAR(64) NOT NULL COMMENT '商品编码',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    amount VARCHAR(32) NOT NULL COMMENT '金额',
    status VARCHAR(32) NOT NULL DEFAULT 'PAID' COMMENT '状态',
    create_time VARCHAR(32) NOT NULL COMMENT '下单时间',
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理端订单表';

-- ========== 人工上门 ==========

DROP TABLE IF EXISTS home_service_provider;

CREATE TABLE home_service_provider (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(64) NOT NULL DEFAULT '' COMMENT '姓名',
    phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    skills VARCHAR(255) NOT NULL DEFAULT '' COMMENT '技能标签，逗号分隔：清洁,维修,烹饪',
    latitude DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
    rating DECIMAL(2,1) NOT NULL DEFAULT 5.0 COMMENT '评分 1~5',
    accept_rate DECIMAL(5,1) NOT NULL DEFAULT 100.0 COMMENT '接单率 0~100',
    order_count INT NOT NULL DEFAULT 0 COMMENT '历史接单数',
    status INT NOT NULL DEFAULT 1 COMMENT '状态 0=离线 1=在线 2=忙碌',
    service_radius INT NOT NULL DEFAULT 10 COMMENT '服务半径（公里）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_skills (skills(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工上门 — 服务者表';

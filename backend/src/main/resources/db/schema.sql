-- Todo 待办事项系统建表脚本
-- 数据库 todo_db 需提前创建（应用连接 URL 已指定库名）
-- Docker 环境下由 docker-compose 中 MYSQL_DATABASE 自动创建

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(50)           DEFAULT NULL COMMENT '昵称',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 待办事项表
CREATE TABLE IF NOT EXISTS `todo` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       NOT NULL COMMENT '所属用户 ID',
    `title`       VARCHAR(200) NOT NULL COMMENT '标题',
    `description` VARCHAR(500)          DEFAULT NULL COMMENT '描述',
    `completed`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否完成：0 未完成 1 已完成',
    `priority`    TINYINT      NOT NULL DEFAULT 1 COMMENT '优先级：0 低 1 中 2 高',
    `due_date`    DATETIME              DEFAULT NULL COMMENT '截止日期',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

-- 兼容已有数据库：若表已存在但缺少新字段，则补充添加
-- priority 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'todo' AND COLUMN_NAME = 'priority');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `todo` ADD COLUMN `priority` TINYINT NOT NULL DEFAULT 1 COMMENT ''优先级：0 低 1 中 2 高'' AFTER `completed`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- due_date 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'todo' AND COLUMN_NAME = 'due_date');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `todo` ADD COLUMN `due_date` DATETIME DEFAULT NULL COMMENT ''截止日期'' AFTER `priority`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

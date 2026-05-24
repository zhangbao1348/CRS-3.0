ALTER TABLE tenant_channels
    ADD COLUMN cancel_failure_requires_manual_intervention TINYINT(1) NOT NULL DEFAULT 1 COMMENT '取消失败时是否需要人工介入';

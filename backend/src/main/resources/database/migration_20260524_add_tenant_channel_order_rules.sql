ALTER TABLE tenant_channels
    ADD COLUMN prepaid_order_requires_payment TINYINT(1) NOT NULL DEFAULT 1 COMMENT '预付订单是否需要支付',
    ADD COLUMN cancel_order_checks_cancellation_rule TINYINT(1) NOT NULL DEFAULT 1 COMMENT '取消订单是否校验取消规则';

-- 回填历史支付流水的租户归属，来源只取其关联订单，避免客户端或手工值污染。
UPDATE reservation_payment payment
INNER JOIN reservation reservation ON reservation.id = payment.reservation_id
SET payment.tenant_id = reservation.tenant_id
WHERE payment.tenant_id IS NULL;

-- 支付流水必须具备租户归属；订单与交易号组合唯一以提供数据库级幂等兜底。
ALTER TABLE reservation_payment
    MODIFY COLUMN tenant_id INT NOT NULL,
    ADD UNIQUE KEY uk_reservation_payment_transaction (reservation_id, transaction_id);

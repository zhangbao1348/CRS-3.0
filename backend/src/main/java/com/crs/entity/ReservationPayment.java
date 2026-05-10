package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预订支付流水实体类 (ReservationPayment)
 * 
 * <p>本类对应数据库中的 `reservation_payment` 表，记录了订单的支付、退款及预授权等财务流水信息。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation_payment")
public class ReservationPayment {

    /** 支付记录主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 关联的预订 ID */
    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;

    /** 
     * 支付方式
     * 可选值：wechat(微信), alipay(支付宝), credit_card(信用卡), cash(现金), pre_auth(预授权)
     */
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    /** 
     * 流水类型
     * payment-支付, refund-退款, pre_auth-预授权
     */
    @Column(name = "payment_type", nullable = false, length = 20)
    private String paymentType = "payment";

    /** 支付或退款金额 */
    @Column(name = "payment_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paymentAmount;

    /** 外部支付平台的交易流水号 */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /** 信用卡末四位 (仅信用卡支付时记录) */
    @Column(name = "credit_card_last4", length = 4)
    private String creditCardLast4;

    /** 信用卡有效期 (格式 MM/YY) */
    @Column(name = "credit_card_expiry", length = 10)
    private String creditCardExpiry;

    /** 支付状态：pending-处理中, success-成功, failed-失败, cancelled-已取消 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "pending";

    /** 实际支付完成时间 */
    @Column(name = "paid_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}


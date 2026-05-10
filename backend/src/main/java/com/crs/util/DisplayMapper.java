package com.crs.util;

import java.util.Map;

/**
 * 显示映射工具类 (DisplayMapper)
 * 
 * <p>本类充当系统内部状态编码 (System Code) 与前端展示标签 (Display Label) 之间的桥梁。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *     <li>将订单状态、支付状态、证件类型等业务编码转换为友好的中文字符串。</li>
 *     <li>为前端提供统一的颜色标签映射，增强 UI 的可读性。</li>
 *     <li>提供渠道图标、操作记录动作等元数据的映射逻辑。</li>
 * </ul>
 */
public class DisplayMapper {

    /** 订单状态映射表 */
    private static final Map<String, String> RESERVATION_STATUS = Map.ofEntries(
            Map.entry("pending", "待确认"),
            Map.entry("confirmed", "已确认"),
            Map.entry("cancelled", "已取消"),
            Map.entry("checked_in", "已入住"),
            Map.entry("checked_out", "已离店"),
            Map.entry("no_show", "未到"),
            Map.entry("cancel_failed", "取消失败")
    );

    /** 订单状态对应的 Ant Design 颜色标签 */
    private static final Map<String, String> STATUS_COLOR = Map.ofEntries(
            Map.entry("pending", "orange"),
            Map.entry("confirmed", "green"),
            Map.entry("cancelled", "red"),
            Map.entry("checked_in", "blue"),
            Map.entry("checked_out", "default"),
            Map.entry("no_show", "volcano"),
            Map.entry("cancel_failed", "red")
    );

    /** 担保类型映射表 */
    private static final Map<String, String> GUARANTEE_TYPE = Map.ofEntries(
            Map.entry("none", "无担保"),
            Map.entry("guarantee", "担保"),
            Map.entry("prepay", "预付"),
            Map.entry("credit_card", "信用卡担保"),
            Map.entry("deposit", "押金担保")
    );

    /** 订单支付状态映射表 */
    private static final Map<String, String> PAYMENT_STATUS = Map.ofEntries(
            Map.entry("unpaid", "未支付"),
            Map.entry("paid", "已支付"),
            Map.entry("partial", "部分支付"),
            Map.entry("refunded", "已退款"),
            Map.entry("pending_refund", "退款中")
    );

    /** 支付流水状态映射表 */
    private static final Map<String, String> PAYMENT_RECORD_STATUS = Map.ofEntries(
            Map.entry("pending", "待支付"),
            Map.entry("success", "支付成功"),
            Map.entry("failed", "支付失败"),
            Map.entry("refunding", "退款中"),
            Map.entry("refunded", "已退款")
    );

    /** 优惠/折扣类型映射表 */
    private static final Map<String, String> DISCOUNT_TYPE = Map.ofEntries(
            Map.entry("percentage", "折扣"),
            Map.entry("fixed", "立减"),
            Map.entry("free_night", "免费房"),
            Map.entry("upgrade", "升级")
    );

    /** 促销活动提供方映射表 */
    private static final Map<String, String> PROMOTION_PROVIDER = Map.ofEntries(
            Map.entry("hotel", "酒店"),
            Map.entry("channel", "渠道"),
            Map.entry("platform", "平台"),
            Map.entry("member", "会员")
    );

    /** 客人证件类型映射表 */
    private static final Map<String, String> ID_TYPE = Map.ofEntries(
            Map.entry("ID_CARD", "身份证"),
            Map.entry("PASSPORT", "护照"),
            Map.entry("HK_MACAO", "港澳通行证"),
            Map.entry("TW_PASS", "台胞证"),
            Map.entry("FOREIGN_PERMANENT", "外国人永久居留证")
    );

    /** 支付方式映射表 */
    private static final Map<String, String> PAYMENT_METHOD = Map.ofEntries(
            Map.entry("credit_card", "信用卡"),
            Map.entry("debit_card", "借记卡"),
            Map.entry("prepay", "预付"),
            Map.entry("pay_on_arrival", "到店付"),
            Map.entry("alipay", "支付宝"),
            Map.entry("wechat_pay", "微信支付"),
            Map.entry("bank_transfer", "银行转账")
    );

    /** 常用预订渠道图标 (Emoji 占位) */
    private static final Map<String, String> CHANNEL_ICON = Map.ofEntries(
            Map.entry("ctrip", "✈️"),
            Map.entry("ctripa", "✈️"),
            Map.entry("fliggy", "🐷"),
            Map.entry("fliggya", "🐷"),
            Map.entry("meituan", "🟢")
    );

    /** 历史记录动作映射表 */
    private static final Map<String, String> HISTORY_ACTION = Map.ofEntries(
            Map.entry("pending", "CREATE"),
            Map.entry("confirmed", "CONFIRM"),
            Map.entry("cancelled", "CANCEL"),
            Map.entry("checked_in", "CHECK_IN"),
            Map.entry("checked_out", "CHECK_OUT"),
            Map.entry("no_show", "NO_SHOW")
    );

    /** 历史记录内容说明映射表 */
    private static final Map<String, String> HISTORY_CONTENT = Map.ofEntries(
            Map.entry("pending", "创建订单"),
            Map.entry("confirmed", "确认订单"),
            Map.entry("cancelled", "取消订单"),
            Map.entry("checked_in", "客人入住"),
            Map.entry("checked_out", "客人离店"),
            Map.entry("no_show", "客人未到")
    );

    /** 增值包/礼遇类型名称映射表 */
    private static final Map<String, String> PACKAGE_TYPE_NAME = Map.ofEntries(
            Map.entry("breakfast", "早餐"),
            Map.entry("lunch", "午餐"),
            Map.entry("dinner", "晚餐"),
            Map.entry("afternoon_tea", "下午茶"),
            Map.entry("minibar", "迷你吧"),
            Map.entry("spa", "SPA/水疗"),
            Map.entry("parking", "停车"),
            Map.entry("airport_transfer", "接送机"),
            Map.entry("laundry", "洗衣"),
            Map.entry("gym", "健身"),
            Map.entry("pool", "泳池"),
            Map.entry("wifi", "上网"),
            Map.entry("voucher", "代金券"),
            Map.entry("gift", "礼品"),
            Map.entry("upgrade", "升级"),
            Map.entry("late_checkout", "延迟退房")
    );

    /**
     * 通用的解析工具方法。
     * 
     * @param mapping 映射表
     * @param key 待解析的编码
     * @param defaultValue 默认值（若 key 为 null 或找不到映射）
     * @return 映射后的字符串
     */
    private static String resolve(Map<String, String> mapping, String key, String defaultValue) {
        if (key == null) return defaultValue;
        return mapping.getOrDefault(key, defaultValue != null ? defaultValue : key);
    }

    /** 映射订单状态文字 */
    public static String reservationStatus(String status) {
        return resolve(RESERVATION_STATUS, status, "未知");
    }

    /** 映射订单状态颜色 */
    public static String statusColor(String status) {
        return resolve(STATUS_COLOR, status, "default");
    }

    /** 映射担保类型文字 */
    public static String guaranteeType(String type) {
        return resolve(GUARANTEE_TYPE, type, "-");
    }

    /** 映射支付状态文字 */
    public static String paymentStatus(String status) {
        return resolve(PAYMENT_STATUS, status, "-");
    }

    /** 映射支付流水状态文字 */
    public static String paymentRecordStatus(String status) {
        return resolve(PAYMENT_RECORD_STATUS, status, "-");
    }

    /** 映射优惠类型文字 */
    public static String discountType(String type) {
        return resolve(DISCOUNT_TYPE, type, "-");
    }

    /** 映射促销提供方文字 */
    public static String promotionProvider(String provider) {
        return resolve(PROMOTION_PROVIDER, provider, "-");
    }

    /** 映射证件类型文字 */
    public static String idType(String type) {
        return resolve(ID_TYPE, type, "-");
    }

    /** 映射支付方式文字 */
    public static String paymentMethod(String method) {
        return resolve(PAYMENT_METHOD, method, "-");
    }

    /**
     * 根据渠道代码获取图标 Emoji。
     * 
     * @param channelCode 渠道代码
     * @return 图标 Emoji，若未找到则返回地球图标
     */
    public static String channelIcon(String channelCode) {
        if (channelCode == null) return "🌐";
        return CHANNEL_ICON.getOrDefault(channelCode.toLowerCase(), "🌐");
    }

    /** 映射历史记录动作编码 */
    public static String historyAction(String status) {
        return resolve(HISTORY_ACTION, status, "STATUS_CHANGE");
    }

    /** 映射历史记录操作内容文字 */
    public static String historyContent(String status) {
        String content = resolve(HISTORY_CONTENT, status, null);
        return content != null ? content : "状态变更";
    }

    /** 映射增值包类型名称 */
    public static String packageTypeName(String type) {
        return resolve(PACKAGE_TYPE_NAME, type, "其他");
    }
}


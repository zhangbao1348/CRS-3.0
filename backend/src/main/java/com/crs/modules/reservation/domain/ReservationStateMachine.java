package com.crs.modules.reservation.domain;

import java.util.Map;
import java.util.Set;

/**
 * 订单生命周期状态机。
 *
 * <p>该类只表达允许的业务跃迁，不包含持久化、库存或通知副作用，便于所有入口
 * 复用同一规则并进行纯单元测试。</p>
 */
public final class ReservationStateMachine {

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "pending", Set.of("confirmed", "cancelled"),
            "pending_payment", Set.of("confirmed", "cancelled"),
            "confirmed", Set.of("checked_in", "cancelled", "no_show"),
            "checked_in", Set.of("checked_out")
    );

    private ReservationStateMachine() {
    }

    /** 判断状态跃迁是否符合订单生命周期。 */
    public static boolean canTransition(String from, String to) {
        Set<String> targets = ALLOWED_TRANSITIONS.get(from);
        return targets != null && targets.contains(to);
    }

    /** 校验状态跃迁，不合法时以稳定业务消息拒绝。 */
    public static void requireTransition(String from, String to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("不允许的状态变更：" + from + " → " + to);
        }
    }
}

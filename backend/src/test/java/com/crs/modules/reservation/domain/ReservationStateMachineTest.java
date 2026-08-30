package com.crs.modules.reservation.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationStateMachineTest {

    @Test
    void shouldAllowOnlyDocumentedLifecycleTransitions() {
        assertTrue(ReservationStateMachine.canTransition("pending", "confirmed"));
        assertTrue(ReservationStateMachine.canTransition("pending_payment", "cancelled"));
        assertTrue(ReservationStateMachine.canTransition("confirmed", "checked_in"));
        assertTrue(ReservationStateMachine.canTransition("confirmed", "no_show"));
        assertTrue(ReservationStateMachine.canTransition("checked_in", "checked_out"));

        assertFalse(ReservationStateMachine.canTransition("checked_in", "cancelled"));
        assertFalse(ReservationStateMachine.canTransition("checked_out", "confirmed"));
        assertFalse(ReservationStateMachine.canTransition("cancelled", "confirmed"));
    }

    @Test
    void shouldRejectInvalidTransitionWithReadableMessage() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> ReservationStateMachine.requireTransition("checked_out", "confirmed"));

        assertTrue(exception.getMessage().contains("checked_out"));
        assertTrue(exception.getMessage().contains("confirmed"));
    }
}

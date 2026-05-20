package br.gov.sifap.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    @DisplayName("REQ-PAY-003: G can transition to P, D, or E")
    void generated_canTransitionToFinalStates() {
        assertTrue(PaymentStatus.G.canTransitionTo(PaymentStatus.P));
        assertTrue(PaymentStatus.G.canTransitionTo(PaymentStatus.D));
        assertTrue(PaymentStatus.G.canTransitionTo(PaymentStatus.E));
    }

    @Test
    @DisplayName("REQ-PAY-003: No reverse transitions from P, D, E")
    void finalStates_cannotTransition() {
        assertFalse(PaymentStatus.P.canTransitionTo(PaymentStatus.G));
        assertFalse(PaymentStatus.P.canTransitionTo(PaymentStatus.D));
        assertFalse(PaymentStatus.D.canTransitionTo(PaymentStatus.P));
        assertFalse(PaymentStatus.E.canTransitionTo(PaymentStatus.G));
    }
}

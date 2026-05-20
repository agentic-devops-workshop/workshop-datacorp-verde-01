package br.gov.sifap.payment.domain;

/**
 * Payment lifecycle: G (generated) → P (paid) | D (returned) | E (error).
 * No reverse transitions allowed.
 * @implements REQ-PAY-003
 */
public enum PaymentStatus {
    G, // Generated
    P, // Paid
    D, // Returned (devolvido)
    E; // Error

    public boolean canTransitionTo(PaymentStatus target) {
        return this == G && (target == P || target == D || target == E);
    }
}

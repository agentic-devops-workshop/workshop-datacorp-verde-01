package br.gov.sifap.payment.domain;

/**
 * Payment type: N (normal) or D (december/13th salary).
 * @implements REQ-PAY-002
 */
public enum PaymentType {
    N, // Normal
    D  // December (13° + abono)
}

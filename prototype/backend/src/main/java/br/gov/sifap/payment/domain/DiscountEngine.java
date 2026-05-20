package br.gov.sifap.payment.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Discount engine: applies social contribution, union dues, and judicial discounts.
 * Non-judicial discounts are capped at 30% of gross value.
 * Judicial discounts have NO cap (legal precedence).
 *
 * @implements REQ-DSC-001, REQ-DSC-002, REQ-DSC-003, REQ-DSC-005
 */
public final class DiscountEngine {

    private static final BigDecimal NON_JUDICIAL_CAP = new BigDecimal("0.30");

    private DiscountEngine() {}

    /**
     * Social contribution: progressive 4 brackets.
     * @implements REQ-DSC-003
     */
    public static BigDecimal socialContribution(BigDecimal grossValue) {
        BigDecimal rate;
        if (grossValue.compareTo(new BigDecimal("500")) <= 0) {
            rate = new BigDecimal("0.03");
        } else if (grossValue.compareTo(new BigDecimal("1000")) <= 0) {
            rate = new BigDecimal("0.05");
        } else if (grossValue.compareTo(new BigDecimal("2000")) <= 0) {
            rate = new BigDecimal("0.07");
        } else {
            rate = new BigDecimal("0.09");
        }
        return grossValue.multiply(rate).setScale(2, RoundingMode.DOWN);
    }

    /**
     * Union discount: fixed 1% of gross.
     * @implements REQ-DSC-005
     */
    public static BigDecimal unionDiscount(BigDecimal grossValue) {
        return grossValue.multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.DOWN);
    }

    /**
     * Applies non-judicial cap (30% of gross). Judicial discounts bypass the cap.
     * @implements REQ-DSC-001, REQ-DSC-002
     */
    public static BigDecimal applyNonJudicialCap(BigDecimal totalNonJudicial, BigDecimal grossValue) {
        BigDecimal cap = grossValue.multiply(NON_JUDICIAL_CAP).setScale(2, RoundingMode.DOWN);
        if (totalNonJudicial.compareTo(cap) > 0) {
            return cap;
        }
        return totalNonJudicial;
    }

    /**
     * Calculates total discount: non-judicial (capped at 30%) + judicial (no cap).
     * @implements REQ-DSC-001, REQ-DSC-002
     */
    public static BigDecimal totalDiscount(BigDecimal nonJudicialTotal, BigDecimal judicialTotal, BigDecimal grossValue) {
        BigDecimal cappedNonJudicial = applyNonJudicialCap(nonJudicialTotal, grossValue);
        return cappedNonJudicial.add(judicialTotal);
    }
}

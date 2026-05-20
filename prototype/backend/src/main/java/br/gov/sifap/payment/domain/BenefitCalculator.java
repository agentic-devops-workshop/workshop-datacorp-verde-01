package br.gov.sifap.payment.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

/**
 * Core benefit calculation engine.
 * Formula: VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RND × FATOR-IDADE × (1 + FATOR-REAJ)
 * Result is TRUNCATED to 2 decimal places.
 *
 * @implements REQ-CALC-003, REQ-CALC-004, REQ-CALC-005, REQ-CALC-006, REQ-CALC-007
 */
public final class BenefitCalculator {

    private BenefitCalculator() {}

    /**
     * @implements REQ-CALC-003
     */
    public static BigDecimal regionalFactor(int regionCode) {
        if (regionCode < 1 || regionCode > 27) {
            return BigDecimal.ONE;
        }
        // Placeholder: in production, read from reference_parameter table
        return switch (regionCode) {
            case 1 -> new BigDecimal("1.00");  // AC
            case 2 -> new BigDecimal("1.05");  // AL
            case 3 -> new BigDecimal("1.10");  // AP
            case 4 -> new BigDecimal("1.15");  // AM
            case 5 -> new BigDecimal("1.20");  // BA
            case 6 -> new BigDecimal("1.25");  // CE
            case 7 -> new BigDecimal("1.32");  // Nordeste generic
            case 8 -> new BigDecimal("1.10");  // ES
            case 9 -> new BigDecimal("1.00");  // GO
            case 10 -> new BigDecimal("1.05"); // MA
            case 11 -> new BigDecimal("1.00"); // MT
            case 12 -> new BigDecimal("1.00"); // MS
            case 13 -> new BigDecimal("1.00"); // MG
            case 14 -> new BigDecimal("1.30"); // PA
            case 15 -> new BigDecimal("1.32"); // PB
            case 16 -> new BigDecimal("1.25"); // PR
            case 17 -> new BigDecimal("1.35"); // PE
            case 18 -> new BigDecimal("1.40"); // PI
            case 19 -> new BigDecimal("1.00"); // RJ
            case 20 -> new BigDecimal("1.30"); // RN
            case 21 -> new BigDecimal("1.00"); // RS
            case 22 -> new BigDecimal("1.20"); // RO
            case 23 -> new BigDecimal("1.30"); // RR
            case 24 -> new BigDecimal("1.00"); // SC
            case 25 -> new BigDecimal("1.00"); // SP
            case 26 -> new BigDecimal("1.25"); // SE
            case 27 -> new BigDecimal("1.35"); // TO
            default -> BigDecimal.ONE;
        };
    }

    /**
     * @implements REQ-CALC-004
     */
    public static BigDecimal familyFactor(int numDependents) {
        if (numDependents <= 0) return new BigDecimal("1.00");
        if (numDependents <= 2) return BigDecimal.ONE.add(new BigDecimal("0.05").multiply(BigDecimal.valueOf(numDependents)));
        if (numDependents <= 4) return new BigDecimal("1.10").add(new BigDecimal("0.03").multiply(BigDecimal.valueOf(numDependents - 2)));
        return new BigDecimal("1.16").add(new BigDecimal("0.02").multiply(BigDecimal.valueOf(numDependents - 4)));
    }

    /**
     * @implements REQ-CALC-005
     */
    public static BigDecimal incomeFactor(BigDecimal familyIncome) {
        if (familyIncome.compareTo(new BigDecimal("300")) <= 0) return new BigDecimal("1.00");
        if (familyIncome.compareTo(new BigDecimal("600")) <= 0) return new BigDecimal("0.85");
        if (familyIncome.compareTo(new BigDecimal("1000")) <= 0) return new BigDecimal("0.70");
        if (familyIncome.compareTo(new BigDecimal("1500")) <= 0) return new BigDecimal("0.55");
        return new BigDecimal("0.40");
    }

    /**
     * @implements REQ-CALC-006
     */
    public static BigDecimal ageFactor(LocalDate birthDate, LocalDate referenceDate) {
        int age = Period.between(birthDate, referenceDate).getYears();
        if (age >= 65) return new BigDecimal("1.15");
        if (age >= 60) return new BigDecimal("1.10");
        if (age < 18) return new BigDecimal("1.05");
        return new BigDecimal("1.00");
    }

    /**
     * Main formula: VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RND × FATOR-IDADE × (1 + FATOR-REAJ)
     * Result is TRUNCATED (not rounded) to 2 decimal places.
     * @implements REQ-CALC-007
     */
    public static BigDecimal calculate(BigDecimal baseValue, BigDecimal regionalFactor,
                                        BigDecimal familyFactor, BigDecimal incomeFactor,
                                        BigDecimal ageFactor, BigDecimal adjustmentFactor) {
        BigDecimal result = baseValue
                .multiply(regionalFactor)
                .multiply(familyFactor)
                .multiply(incomeFactor)
                .multiply(ageFactor)
                .multiply(BigDecimal.ONE.add(adjustmentFactor));
        return truncate(result);
    }

    /**
     * 13th salary: VLR-BASE × FATOR-REG × FATOR-IDADE (no family/income factors).
     * @implements REQ-CALC-008
     */
    public static BigDecimal calculateThirteenth(BigDecimal baseValue, BigDecimal regionalFactor,
                                                  BigDecimal ageFactor) {
        BigDecimal result = baseValue.multiply(regionalFactor).multiply(ageFactor);
        return truncate(result);
    }

    /**
     * Truncates to 2 decimal places: ×100, integer, ÷100
     */
    private static BigDecimal truncate(BigDecimal value) {
        return value.setScale(2, RoundingMode.DOWN);
    }
}

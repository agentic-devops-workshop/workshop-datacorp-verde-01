package br.gov.sifap.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BenefitCalculatorTest {

    @Test
    @DisplayName("REQ-CALC-003: Regional factor for code 7 (Nordeste) should be 1.32")
    void regionalFactor_code7_returns132() {
        assertEquals(new BigDecimal("1.32"), BenefitCalculator.regionalFactor(7));
    }

    @Test
    @DisplayName("REQ-CALC-003: Region code outside 1-27 defaults to 1.00")
    void regionalFactor_invalidCode_returnsOne() {
        assertEquals(0, BigDecimal.ONE.compareTo(BenefitCalculator.regionalFactor(99)));
        assertEquals(0, BigDecimal.ONE.compareTo(BenefitCalculator.regionalFactor(0)));
    }

    @Test
    @DisplayName("REQ-CALC-004: 0 dependents = factor 1.00")
    void familyFactor_zeroDependents_returns100() {
        assertEquals(new BigDecimal("1.00"), BenefitCalculator.familyFactor(0));
    }

    @Test
    @DisplayName("REQ-CALC-004: 2 dependents = 1.00 + 0.05×2 = 1.10")
    void familyFactor_twoDependents_returns110() {
        assertEquals(0, new BigDecimal("1.10").compareTo(BenefitCalculator.familyFactor(2)));
    }

    @Test
    @DisplayName("REQ-CALC-004: 4 dependents = 1.10 + 0.03×2 = 1.16")
    void familyFactor_fourDependents_returns116() {
        assertEquals(0, new BigDecimal("1.16").compareTo(BenefitCalculator.familyFactor(4)));
    }

    @Test
    @DisplayName("REQ-CALC-004: 6 dependents = 1.16 + 0.02×2 = 1.20")
    void familyFactor_sixDependents_returns120() {
        assertEquals(0, new BigDecimal("1.20").compareTo(BenefitCalculator.familyFactor(6)));
    }

    @Test
    @DisplayName("REQ-CALC-005: Income ≤300 = factor 1.00")
    void incomeFactor_250_returns100() {
        assertEquals(new BigDecimal("1.00"), BenefitCalculator.incomeFactor(new BigDecimal("250")));
    }

    @Test
    @DisplayName("REQ-CALC-005: Income ≤600 = factor 0.85")
    void incomeFactor_600_returns085() {
        assertEquals(new BigDecimal("0.85"), BenefitCalculator.incomeFactor(new BigDecimal("600")));
    }

    @Test
    @DisplayName("REQ-CALC-005: Income >1500 = factor 0.40")
    void incomeFactor_2000_returns040() {
        assertEquals(new BigDecimal("0.40"), BenefitCalculator.incomeFactor(new BigDecimal("2000")));
    }

    @Test
    @DisplayName("REQ-CALC-006: Age ≥65 = factor 1.15")
    void ageFactor_66years_returns115() {
        LocalDate birthDate = LocalDate.of(1960, 1, 1);
        LocalDate ref = LocalDate.of(2026, 5, 20);
        assertEquals(new BigDecimal("1.15"), BenefitCalculator.ageFactor(birthDate, ref));
    }

    @Test
    @DisplayName("REQ-CALC-006: Age ≥60 <65 = factor 1.10")
    void ageFactor_62years_returns110() {
        LocalDate birthDate = LocalDate.of(1964, 1, 1);
        LocalDate ref = LocalDate.of(2026, 5, 20);
        assertEquals(new BigDecimal("1.10"), BenefitCalculator.ageFactor(birthDate, ref));
    }

    @Test
    @DisplayName("REQ-CALC-006: Age <18 = factor 1.05")
    void ageFactor_16years_returns105() {
        LocalDate birthDate = LocalDate.of(2010, 1, 1);
        LocalDate ref = LocalDate.of(2026, 5, 20);
        assertEquals(new BigDecimal("1.05"), BenefitCalculator.ageFactor(birthDate, ref));
    }

    @Test
    @DisplayName("REQ-CALC-006: Age 18-59 = factor 1.00")
    void ageFactor_35years_returns100() {
        LocalDate birthDate = LocalDate.of(1991, 1, 1);
        LocalDate ref = LocalDate.of(2026, 5, 20);
        assertEquals(new BigDecimal("1.00"), BenefitCalculator.ageFactor(birthDate, ref));
    }

    @Test
    @DisplayName("REQ-CALC-007: Main formula result is truncated, not rounded")
    void calculate_truncatesResult() {
        BigDecimal base = new BigDecimal("500");
        BigDecimal reg = new BigDecimal("1.20");
        BigDecimal fam = new BigDecimal("1.10");
        BigDecimal inc = new BigDecimal("0.85");
        BigDecimal age = new BigDecimal("1.15");
        BigDecimal adj = new BigDecimal("0.03");

        BigDecimal result = BenefitCalculator.calculate(base, reg, fam, inc, age, adj);

        // 500 × 1.20 × 1.10 × 0.85 × 1.15 × 1.03 = 664.5033
        // Truncated to 2 decimals = 664.50
        assertEquals(new BigDecimal("664.50"), result);
    }

    @Test
    @DisplayName("REQ-CALC-008: 13th salary uses only base × regional × age factors")
    void calculateThirteenth_usesOnlyThreeFactors() {
        BigDecimal base = new BigDecimal("1000");
        BigDecimal reg = new BigDecimal("1.20");
        BigDecimal age = new BigDecimal("1.15");

        BigDecimal result = BenefitCalculator.calculateThirteenth(base, reg, age);

        assertEquals(new BigDecimal("1380.00"), result);
    }
}

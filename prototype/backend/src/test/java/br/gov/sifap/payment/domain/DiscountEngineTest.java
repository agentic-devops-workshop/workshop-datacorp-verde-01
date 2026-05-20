package br.gov.sifap.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DiscountEngineTest {

    @Test
    @DisplayName("REQ-DSC-003: Gross ≤500 → 3% social contribution")
    void socialContribution_400_returns12() {
        assertEquals(new BigDecimal("12.00"), DiscountEngine.socialContribution(new BigDecimal("400")));
    }

    @Test
    @DisplayName("REQ-DSC-003: Gross ≤1000 → 5% social contribution")
    void socialContribution_800_returns40() {
        assertEquals(new BigDecimal("40.00"), DiscountEngine.socialContribution(new BigDecimal("800")));
    }

    @Test
    @DisplayName("REQ-DSC-003: Gross ≤2000 → 7% social contribution")
    void socialContribution_1500_returns105() {
        assertEquals(new BigDecimal("105.00"), DiscountEngine.socialContribution(new BigDecimal("1500")));
    }

    @Test
    @DisplayName("REQ-DSC-003: Gross >2000 → 9% social contribution")
    void socialContribution_3000_returns270() {
        assertEquals(new BigDecimal("270.00"), DiscountEngine.socialContribution(new BigDecimal("3000")));
    }

    @Test
    @DisplayName("REQ-DSC-005: Union discount is 1% of gross")
    void unionDiscount_1000_returns10() {
        assertEquals(new BigDecimal("10.00"), DiscountEngine.unionDiscount(new BigDecimal("1000")));
    }

    @Test
    @DisplayName("REQ-DSC-001: Non-judicial discount capped at 30% of gross")
    void applyNonJudicialCap_exceeds30percent_isCapped() {
        BigDecimal gross = new BigDecimal("1000");
        BigDecimal discount = new BigDecimal("400");
        assertEquals(new BigDecimal("300.00"), DiscountEngine.applyNonJudicialCap(discount, gross));
    }

    @Test
    @DisplayName("REQ-DSC-001: Non-judicial discount under 30% is not capped")
    void applyNonJudicialCap_under30percent_isNotCapped() {
        BigDecimal gross = new BigDecimal("1000");
        BigDecimal discount = new BigDecimal("200");
        assertEquals(new BigDecimal("200"), DiscountEngine.applyNonJudicialCap(discount, gross));
    }

    @Test
    @DisplayName("REQ-DSC-002: Judicial discount bypasses the 30% cap")
    void totalDiscount_judicialBypassesCap() {
        BigDecimal gross = new BigDecimal("1000");
        BigDecimal nonJudicial = new BigDecimal("400"); // would be capped to 300
        BigDecimal judicial = new BigDecimal("500");    // no cap

        BigDecimal total = DiscountEngine.totalDiscount(nonJudicial, judicial, gross);
        // 300 (capped non-judicial) + 500 (judicial) = 800
        assertEquals(new BigDecimal("800.00"), total);
    }
}

package br.gov.sifap.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPFTest {

    @Test
    @DisplayName("Valid CPF passes module-11 check")
    void validCpf_isAccepted() {
        assertDoesNotThrow(() -> new CPF("52998224725"));
    }

    @Test
    @DisplayName("CPF with all same digits is rejected")
    void allSameDigits_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CPF("11111111111"));
    }

    @Test
    @DisplayName("CPF with wrong length is rejected")
    void wrongLength_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CPF("1234567890"));
    }

    @Test
    @DisplayName("CPF masked format hides first 3 digits (LGPD)")
    void masked_hidesFirstThreeDigits() {
        CPF cpf = new CPF("52998224725");
        assertEquals("***.982.247-25", cpf.masked());
    }

    @Test
    @DisplayName("CPF formatted shows NNN.NNN.NNN-NN")
    void formatted_showsFullFormat() {
        CPF cpf = new CPF("52998224725");
        assertEquals("529.982.247-25", cpf.formatted());
    }

    @Test
    @DisplayName("toString returns masked (LGPD-safe for logs)")
    void toString_returnsMasked() {
        CPF cpf = new CPF("52998224725");
        assertTrue(cpf.toString().startsWith("***"));
    }
}

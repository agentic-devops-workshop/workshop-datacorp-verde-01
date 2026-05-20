package br.gov.sifap.shared.domain;

import java.util.Objects;

/**
 * Value Object representing a Brazilian CPF with module-11 validation.
 * Masks display output for LGPD compliance.
 */
public record CPF(String value) {

    public CPF {
        Objects.requireNonNull(value, "CPF must not be null");
        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 11) {
            throw new IllegalArgumentException("CPF must have 11 digits");
        }
        if (!isValid(digits)) {
            throw new IllegalArgumentException("Invalid CPF: fails module-11 check");
        }
        value = digits;
    }

    /**
     * Returns LGPD-compliant masked format: ***.XXX.XXX-XX
     */
    public String masked() {
        return "***." + value.substring(3, 6) + "." + value.substring(6, 9) + "-" + value.substring(9);
    }

    /**
     * Returns formatted CPF: NNN.NNN.NNN-NN
     */
    public String formatted() {
        return value.substring(0, 3) + "." + value.substring(3, 6) + "."
                + value.substring(6, 9) + "-" + value.substring(9);
    }

    @Override
    public String toString() {
        return masked();
    }

    private static boolean isValid(String digits) {
        if (digits.chars().distinct().count() == 1) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (digits.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        if (firstDigit != (digits.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (digits.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        return secondDigit == (digits.charAt(10) - '0');
    }
}

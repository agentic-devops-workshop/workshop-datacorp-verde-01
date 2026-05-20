package br.gov.sifap.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object for monetary values. Uses truncation (not rounding) at 2 decimal places.
 * Formula: multiply by 100, truncate to integer, divide by 100.
 */
public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "Amount must not be null");
        amount = truncate(amount);
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor));
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money subtract(Money other) {
        BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }
        return new Money(result);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Truncates to 2 decimal places (×100, integer, ÷100) — never rounds.
     */
    private static BigDecimal truncate(BigDecimal value) {
        return value.setScale(2, RoundingMode.DOWN);
    }
}

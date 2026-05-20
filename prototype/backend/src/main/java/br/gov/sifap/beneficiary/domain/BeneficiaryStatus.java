package br.gov.sifap.beneficiary.domain;

/**
 * Beneficiary status. Only 'A' (active) allows payment calculation.
 * @implements REQ-CALC-001
 */
public enum BeneficiaryStatus {
    A, // Active
    S, // Suspended
    C, // Cancelled
    I, // Inactive
    D  // Deceased
}

package br.gov.sifap.payment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "payment", schema = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_id", nullable = false)
    private Long beneficiaryId;

    @Column(name = "cpf", nullable = false, columnDefinition = "CHAR(11)")
    private String cpf;

    @Column(name = "competence_year_month", nullable = false, length = 7)
    private String competenceYearMonth;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "CHAR(1)")
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "CHAR(1)")
    private PaymentType type;

    @Column(name = "gross_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossValue;

    @Column(name = "net_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal netValue;

    @Column(name = "total_discounts", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDiscounts;

    @Column(name = "thirteenth_value", precision = 12, scale = 2)
    private BigDecimal thirteenthValue;

    @Column(name = "christmas_bonus", precision = 12, scale = 2)
    private BigDecimal christmasBonus;

    @Column(name = "social_program_id", nullable = false)
    private Long socialProgramId;

    @Column(name = "batch_run_id")
    private Long batchRunId;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    protected Payment() {}

    public Payment(Long beneficiaryId, String cpf, YearMonth competence,
                   PaymentType type, BigDecimal grossValue, BigDecimal netValue,
                   BigDecimal totalDiscounts, Long socialProgramId) {
        this.beneficiaryId = beneficiaryId;
        this.cpf = cpf;
        this.competenceYearMonth = competence.toString();
        this.status = PaymentStatus.G;
        this.type = type;
        this.grossValue = grossValue;
        this.netValue = netValue;
        this.totalDiscounts = totalDiscounts;
        this.socialProgramId = socialProgramId;
        this.createdAt = LocalDate.now();
    }

    /**
     * Transitions payment status. Throws if transition is invalid.
     * @implements REQ-PAY-003
     */
    public void transitionTo(PaymentStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "Cannot transition from " + this.status + " to " + newStatus);
        }
        this.status = newStatus;
    }

    public Long getId() { return id; }
    public Long getBeneficiaryId() { return beneficiaryId; }
    public String getCpf() { return cpf; }
    public YearMonth getCompetence() { return YearMonth.parse(competenceYearMonth); }
    public PaymentStatus getStatus() { return status; }
    public PaymentType getType() { return type; }
    public BigDecimal getGrossValue() { return grossValue; }
    public BigDecimal getNetValue() { return netValue; }
    public BigDecimal getTotalDiscounts() { return totalDiscounts; }
    public BigDecimal getThirteenthValue() { return thirteenthValue; }
    public BigDecimal getChristmasBonus() { return christmasBonus; }
    public Long getSocialProgramId() { return socialProgramId; }
    public Long getBatchRunId() { return batchRunId; }

    public void setThirteenthValue(BigDecimal thirteenthValue) { this.thirteenthValue = thirteenthValue; }
    public void setChristmasBonus(BigDecimal christmasBonus) { this.christmasBonus = christmasBonus; }
    public void setBatchRunId(Long batchRunId) { this.batchRunId = batchRunId; }
}

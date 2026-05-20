package br.gov.sifap.beneficiary.domain;

import br.gov.sifap.shared.domain.CPF;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "beneficiary", schema = "beneficiary")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf", nullable = false, unique = true, columnDefinition = "CHAR(11)")
    private String cpf;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "CHAR(1)")
    private BeneficiaryStatus status;

    @Column(name = "region_code", nullable = false)
    private Integer regionCode;

    @Column(name = "num_dependents", nullable = false)
    private Integer numDependents;

    @Column(name = "family_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal familyIncome;

    @Column(name = "social_program_id", nullable = false)
    private Long socialProgramId;

    @Column(name = "base_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseValue;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    protected Beneficiary() {}

    public Long getId() { return id; }
    public String getCpf() { return cpf; }
    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
    public BeneficiaryStatus getStatus() { return status; }
    public Integer getRegionCode() { return regionCode; }
    public Integer getNumDependents() { return numDependents; }
    public BigDecimal getFamilyIncome() { return familyIncome; }
    public Long getSocialProgramId() { return socialProgramId; }
    public BigDecimal getBaseValue() { return baseValue; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }

    public void setStatus(BeneficiaryStatus status) { this.status = status; }
    public void setName(String name) { this.name = name; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() {
        return this.status == BeneficiaryStatus.A;
    }

    public CPF cpfValueObject() {
        return new CPF(this.cpf);
    }
}

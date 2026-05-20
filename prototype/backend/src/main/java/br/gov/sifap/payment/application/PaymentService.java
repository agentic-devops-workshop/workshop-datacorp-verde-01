package br.gov.sifap.payment.application;

import br.gov.sifap.beneficiary.application.BeneficiaryService;
import br.gov.sifap.beneficiary.domain.Beneficiary;
import br.gov.sifap.payment.domain.*;
import br.gov.sifap.payment.infrastructure.JpaPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final JpaPaymentRepository paymentRepository;
    private final BeneficiaryService beneficiaryService;

    public PaymentService(JpaPaymentRepository paymentRepository, BeneficiaryService beneficiaryService) {
        this.paymentRepository = paymentRepository;
        this.beneficiaryService = beneficiaryService;
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findByBatchRunId(Long batchRunId) {
        return paymentRepository.findByBatchRunId(batchRunId);
    }

    /**
     * Transitions payment status with guard clause.
     * @implements REQ-PAY-003
     */
    @Transactional
    public Payment transitionStatus(Long paymentId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        payment.transitionTo(newStatus);
        return paymentRepository.save(payment);
    }

    /**
     * Generates payments for all active beneficiaries of a given program.
     * @implements REQ-PAY-001, REQ-CALC-001, REQ-CALC-002
     */
    @Transactional
    public List<Payment> generatePaymentsForProgram(Long programId, YearMonth competence, BigDecimal adjustmentFactor) {
        List<Beneficiary> activeBeneficiaries = beneficiaryService.findAllActiveByProgramId(programId);

        return activeBeneficiaries.stream()
                .filter(Beneficiary::isActive)
                .filter(b -> paymentRepository.findByBeneficiaryIdAndCompetence(b.getId(), competence.toString()).isEmpty())
                .map(b -> createPaymentFor(b, competence, adjustmentFactor))
                .toList();
    }

    private Payment createPaymentFor(Beneficiary b, YearMonth competence, BigDecimal adjustmentFactor) {
        LocalDate refDate = competence.atEndOfMonth();

        BigDecimal regFactor = BenefitCalculator.regionalFactor(b.getRegionCode());
        BigDecimal famFactor = BenefitCalculator.familyFactor(b.getNumDependents());
        BigDecimal incFactor = BenefitCalculator.incomeFactor(b.getFamilyIncome());
        BigDecimal ageFactor = BenefitCalculator.ageFactor(b.getBirthDate(), refDate);

        BigDecimal grossValue = BenefitCalculator.calculate(
                b.getBaseValue(), regFactor, famFactor, incFactor, ageFactor, adjustmentFactor);

        BigDecimal socialContrib = DiscountEngine.socialContribution(grossValue);
        BigDecimal totalDiscounts = DiscountEngine.applyNonJudicialCap(socialContrib, grossValue);

        BigDecimal netValue = grossValue.subtract(totalDiscounts);
        if (netValue.compareTo(BigDecimal.ZERO) < 0) {
            netValue = BigDecimal.ZERO;
        }

        PaymentType type = competence.getMonthValue() == 12 ? PaymentType.D : PaymentType.N;

        Payment payment = new Payment(b.getId(), b.getCpf(), competence, type, grossValue, netValue, totalDiscounts, b.getSocialProgramId());

        if (competence.getMonthValue() == 12) {
            BigDecimal thirteenth = BenefitCalculator.calculateThirteenth(b.getBaseValue(), regFactor, ageFactor);
            payment.setThirteenthValue(thirteenth);
        }

        return paymentRepository.save(payment);
    }
}

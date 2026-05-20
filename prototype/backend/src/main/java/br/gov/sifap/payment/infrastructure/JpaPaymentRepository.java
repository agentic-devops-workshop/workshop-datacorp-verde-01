package br.gov.sifap.payment.infrastructure;

import br.gov.sifap.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.cpf = :cpf AND p.competenceYearMonth = :competence")
    Optional<Payment> findByCpfAndCompetence(String cpf, String competence);

    List<Payment> findByBatchRunId(Long batchRunId);

    @Query("SELECT p FROM Payment p WHERE p.beneficiaryId = :beneficiaryId AND p.competenceYearMonth = :competence")
    Optional<Payment> findByBeneficiaryIdAndCompetence(Long beneficiaryId, String competence);
}

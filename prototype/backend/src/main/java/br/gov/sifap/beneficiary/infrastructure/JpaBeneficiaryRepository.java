package br.gov.sifap.beneficiary.infrastructure;

import br.gov.sifap.beneficiary.domain.Beneficiary;
import br.gov.sifap.beneficiary.domain.BeneficiaryRepository;
import br.gov.sifap.beneficiary.domain.BeneficiaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBeneficiaryRepository extends JpaRepository<Beneficiary, Long>, BeneficiaryRepository {

    Optional<Beneficiary> findByCpf(String cpf);

    @Query("SELECT b FROM Beneficiary b WHERE b.socialProgramId = :programId AND b.status = 'A'")
    List<Beneficiary> findAllActiveByProgramId(Long programId);
}

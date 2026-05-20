package br.gov.sifap.beneficiary.domain;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository {
    Optional<Beneficiary> findById(Long id);
    Optional<Beneficiary> findByCpf(String cpf);
    List<Beneficiary> findAllActiveByProgramId(Long programId);
    Beneficiary save(Beneficiary beneficiary);
}

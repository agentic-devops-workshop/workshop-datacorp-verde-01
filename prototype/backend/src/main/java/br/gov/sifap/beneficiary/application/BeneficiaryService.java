package br.gov.sifap.beneficiary.application;

import br.gov.sifap.beneficiary.domain.Beneficiary;
import br.gov.sifap.beneficiary.domain.BeneficiaryRepository;
import br.gov.sifap.shared.domain.CPF;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BeneficiaryService {

    private final BeneficiaryRepository repository;

    public BeneficiaryService(BeneficiaryRepository repository) {
        this.repository = repository;
    }

    public Optional<Beneficiary> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Beneficiary> findByCpf(String cpf) {
        CPF validated = new CPF(cpf);
        return repository.findByCpf(validated.value());
    }

    public List<Beneficiary> findAllActiveByProgramId(Long programId) {
        return repository.findAllActiveByProgramId(programId);
    }

    @Transactional
    public Beneficiary save(Beneficiary beneficiary) {
        return repository.save(beneficiary);
    }
}

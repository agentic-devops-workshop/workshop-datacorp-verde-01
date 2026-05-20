package br.gov.sifap.beneficiary.infrastructure;

import br.gov.sifap.beneficiary.application.BeneficiaryService;
import br.gov.sifap.beneficiary.domain.Beneficiary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@Tag(name = "Beneficiaries", description = "Beneficiary management")
public class BeneficiaryController {

    private final BeneficiaryService service;

    public BeneficiaryController(BeneficiaryService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find beneficiary by ID")
    public ResponseEntity<Beneficiary> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-program/{programId}")
    @Operation(summary = "List active beneficiaries by social program")
    public List<Beneficiary> findActiveByProgram(@PathVariable Long programId) {
        return service.findAllActiveByProgramId(programId);
    }
}

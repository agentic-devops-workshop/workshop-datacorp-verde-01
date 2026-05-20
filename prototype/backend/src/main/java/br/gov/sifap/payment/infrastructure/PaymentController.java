package br.gov.sifap.payment.infrastructure;

import br.gov.sifap.payment.application.PaymentService;
import br.gov.sifap.payment.domain.Payment;
import br.gov.sifap.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment generation and lifecycle management")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find payment by ID")
    public ResponseEntity<Payment> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate payments for all active beneficiaries of a program")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Payment> generatePayments(@Valid @RequestBody GeneratePaymentsRequest request) {
        return service.generatePaymentsForProgram(
                request.programId(),
                request.competence(),
                request.adjustmentFactor()
        );
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transition payment status (G → P/D/E)")
    public ResponseEntity<Payment> transitionStatus(@PathVariable Long id,
                                                     @Valid @RequestBody TransitionStatusRequest request) {
        try {
            Payment updated = service.transitionStatus(id, request.newStatus());
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    public record GeneratePaymentsRequest(
            @NotNull Long programId,
            @NotNull YearMonth competence,
            @NotNull BigDecimal adjustmentFactor
    ) {}

    public record TransitionStatusRequest(
            @NotNull PaymentStatus newStatus
    ) {}
}

package br.gov.sifap.audit.application;

import br.gov.sifap.audit.domain.AuditEventEntity;
import br.gov.sifap.audit.infrastructure.JpaAuditEventRepository;
import br.gov.sifap.shared.domain.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final JpaAuditEventRepository repository;

    public AuditService(JpaAuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(AuditEvent event) {
        AuditEventEntity entity = new AuditEventEntity(
                event.action(),
                event.entityType(),
                event.entityId(),
                event.userId(),
                event.description()
        );
        repository.save(entity);
    }

    public List<AuditEventEntity> findByEntity(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditEventEntity> findByUser(String userId) {
        return repository.findByUserId(userId);
    }
}

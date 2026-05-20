package br.gov.sifap.audit.infrastructure;

import br.gov.sifap.audit.domain.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
    List<AuditEventEntity> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<AuditEventEntity> findByUserId(String userId);
}

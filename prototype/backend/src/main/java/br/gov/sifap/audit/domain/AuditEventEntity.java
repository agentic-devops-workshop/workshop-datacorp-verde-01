package br.gov.sifap.audit.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_event", schema = "audit")
public class AuditEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", nullable = false, columnDefinition = "CHAR(2)")
    private String action;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "description")
    private String description;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    protected AuditEventEntity() {}

    public AuditEventEntity(String action, String entityType, String entityId,
                            String userId, String description) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.description = description;
        this.timestamp = Instant.now();
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getUserId() { return userId; }
    public String getDescription() { return description; }
    public Instant getTimestamp() { return timestamp; }
}

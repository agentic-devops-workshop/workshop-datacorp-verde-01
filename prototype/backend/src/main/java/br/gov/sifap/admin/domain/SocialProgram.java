package br.gov.sifap.admin.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "social_program", schema = "admin")
public class SocialProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false, columnDefinition = "CHAR(1)")
    private String type; // A=assistencial, P=previdenciario, T=trabalho

    @Column(name = "status", nullable = false, columnDefinition = "CHAR(1)")
    private String status; // A=ativo, I=inativo

    @Column(name = "description")
    private String description;

    protected SocialProgram() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    public boolean isActive() {
        return "A".equals(this.status);
    }

    public boolean isAssistencial() {
        return "A".equals(this.type);
    }
}

package br.gov.sifap.admin.infrastructure;

import br.gov.sifap.admin.domain.SocialProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSocialProgramRepository extends JpaRepository<SocialProgram, Long> {
    List<SocialProgram> findByStatus(String status);
}

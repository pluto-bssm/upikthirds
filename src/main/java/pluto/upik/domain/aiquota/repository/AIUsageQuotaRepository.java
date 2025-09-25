package pluto.upik.domain.aiquota.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.aiquota.data.model.AIUsageQuota;

import java.util.UUID;

@Repository
public interface AIUsageQuotaRepository extends JpaRepository<AIUsageQuota, UUID> {
    // 기본 JPA 메소드로 충분함
}

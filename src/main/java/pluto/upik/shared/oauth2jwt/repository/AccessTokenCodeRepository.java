package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.shared.oauth2jwt.entity.AccessTokenCode;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;

import java.util.UUID;

public interface AccessTokenCodeRepository extends JpaRepository<AccessTokenCode, String> {
    AccessTokenCode findByCode(String code);

    void deleteByCode(String code);
}

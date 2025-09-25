package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;
import pluto.upik.shared.oauth2jwt.entity.RefreshTokenCode;

import java.util.UUID;

public interface RefreshTokenCodeRepository extends JpaRepository<RefreshTokenCode, String> {
    RefreshTokenCode findByCode(String code);

    void deleteByCode(String code);
}

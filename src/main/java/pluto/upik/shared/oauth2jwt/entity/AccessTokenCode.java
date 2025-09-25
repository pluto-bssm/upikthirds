package pluto.upik.shared.oauth2jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "AccessTokenCode")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenCode {

    @Id
    @Column(name = "code", nullable = false, length = 255)
    private String code;

    @Column(name = "accessToken", nullable = false, length = 255)
    private String accessToken;


}
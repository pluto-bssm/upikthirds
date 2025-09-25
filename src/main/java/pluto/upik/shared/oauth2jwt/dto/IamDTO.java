package pluto.upik.shared.oauth2jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 현재 로그인한 사용자의 정보를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IamDTO {
    private UUID id;
    private String username;
    private String name;
    private String email;
    private String role;
    private String provider;
    private String profileImage;
    private boolean authenticated;
}
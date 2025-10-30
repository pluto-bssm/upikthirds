package pluto.upik.domain.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GraphQL 서비스 상태 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealth {
    private String status;
    private String service;

    public static ServiceHealth up(String serviceName) {
        return ServiceHealth.builder()
                .status("UP")
                .service(serviceName)
                .build();
    }

    public static ServiceHealth down(String serviceName) {
        return ServiceHealth.builder()
                .status("DOWN")
                .service(serviceName)
                .build();
    }
}

package pluto.upik.domain.notification.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.notification.data.DTO.NotificationMutation;
import pluto.upik.domain.notification.service.NotificationServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationMutationResolver {

    private final NotificationServiceInterface notificationService;
    private final SecurityUtil securityUtil; // 🔑 SecurityUtil 주입

    @RequireAuth
    @SchemaMapping(typeName = "NotificationMutation", field = "markNotificationAsRead")
    public boolean markNotificationAsRead(NotificationMutation parent, @Argument Long notificationId) {
        log.info("GraphQL 뮤테이션 - 알림 읽음 표시 요청: notificationId={}", notificationId);
        try {
            UUID userId = securityUtil.getCurrentUserId(); // ✅ 실제 로그인 사용자 ID

            boolean result = notificationService.markAsRead(notificationId, userId).isRead();
            log.info("GraphQL 뮤테이션 - 알림 읽음 표시 완료: notificationId={}, userId={}", notificationId, userId);
            return result;
        } catch (Exception e) {
            log.error("GraphQL 뮤테이션 - 알림 읽음 표시 실패: notificationId={}", notificationId, e);
            throw new RuntimeException("알림 읽음 표시 중 오류가 발생했습니다.", e);
        }
    }

//    @SchemaMapping(typeName = "NotificationMutation", field = "markAllNotificationsAsRead")
//    public boolean markAllNotificationsAsRead(NotificationMutation parent) {
//        log.info("GraphQL 뮤테이션 - 모든 알림 읽음 표시 요청");
//        try {
//            UUID userId = securityUtil.getCurrentUserId(); // ✅ 실제 로그인 사용자 ID
//            boolean result = notificationService.markAllAsRead(userId);
//            log.info("GraphQL 뮤테이션 - 모든 알림 읽음 표시 완료: userId={}", userId);
//            return result;
//        } catch (Exception e) {
//            log.error("GraphQL 뮤테이션 - 모든 알림 읽음 표시 실패", e);
//            throw new RuntimeException("모든 알림 읽음 표시 중 오류가 발생했습니다.", e);
//        }
//    }
}

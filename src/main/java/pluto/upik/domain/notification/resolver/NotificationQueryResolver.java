package pluto.upik.domain.notification.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.notification.data.DTO.NotificationQuery;
import pluto.upik.domain.notification.data.DTO.NotificationResponse;
import pluto.upik.domain.notification.service.NotificationServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationQueryResolver {

    private final NotificationServiceInterface notificationService;
    private final SecurityUtil securityUtil;

//    @SchemaMapping(typeName = "NotificationQuery", field = "getNotifications")
//    public List<NotificationResponse> getNotifications(NotificationQuery source) {
//        log.info("GraphQL 쿼리 - 알림 목록 조회 요청");
//        try {
//            UUID userId = securityUtil.getCurrentUserId();
//            List<NotificationResponse> notifications = notificationService.getUnreadNotificationCount(userId);
//            log.info("GraphQL 쿼리 - 알림 목록 조회 완료: 조회된 알림 수={}", notifications.size());
//            return notifications;
//        } catch (Exception e) {
//            log.error("GraphQL 쿼리 - 알림 목록 조회 실패", e);
//            throw e;
//        }
//    }

    @RequireAuth
    @SchemaMapping(typeName = "NotificationQuery", field = "getUnreadNotifications")
    public List<NotificationResponse> getUnreadNotifications(NotificationQuery source) {
        log.info("GraphQL 쿼리 - 읽지 않은 알림 목록 조회 요청");
        try {
            UUID userId = securityUtil.getCurrentUserId();
            List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
            log.info("GraphQL 쿼리 - 읽지 않은 알림 목록 조회 완료: 조회된 알림 수={}", notifications.size());
            return notifications;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 읽지 않은 알림 목록 조회 실패", e);
            throw e;
        }
    }

//    @SchemaMapping(typeName = "NotificationQuery", field = "hasUnreadNotifications")
//    public boolean hasUnreadNotifications(NotificationQuery source) {
//        log.info("GraphQL 쿼리 - 읽지 않은 알림 존재 여부 확인 요청");
//        try {
//            UUID userId = securityUtil.getCurrentUserId();
//            boolean hasUnread = notificationService.getUnreadNotificationCount(userId);
//            log.info("GraphQL 쿼리 - 읽지 않은 알림 존재 여부 확인 완료: 결과={}", hasUnread);
//            return hasUnread;
//        } catch (Exception e) {
//            log.error("GraphQL 쿼리 - 읽지 않은 알림 존재 여부 확인 실패", e);
//            throw e;
//        }
//    }
}

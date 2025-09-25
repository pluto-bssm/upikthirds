package pluto.upik.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.notification.NotificationType;
import pluto.upik.domain.notification.data.DTO.NotificationResponse;
import pluto.upik.domain.notification.data.model.Notification;
import pluto.upik.domain.notification.repository.NotificationRepository;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationServiceInterface {

    private final NotificationRepository notificationRepository;

    /**
     * 사용자의 모든 알림을 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 알림 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 읽지 않은 알림 수를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 수
     */
    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * 알림을 읽음 상태로 표시합니다.
     *
     * @param notificationId 알림 ID
     * @param userId 사용자 ID
     * @return 업데이트된 알림 정보
     */
    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("해당 사용자의 알림이 아닙니다.");
    }

            notification.setRead(true);
        notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    /**
     * 사용자의 모든 알림을 읽음 상태로 표시합니다.
     *
     * @param userId 사용자 ID
     * @return 업데이트된 알림 수
     */
    @Override
    @Transactional
    public int markAllAsRead(UUID userId) {
        // 더미 구현: 빈 목록 반환
        List<Notification> unreadNotifications = new ArrayList<>();

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        notificationRepository.save(notification);
    }

        return unreadNotifications.size();
    }

    /**
     * 투표 종료 알림을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param voteId 투표 ID
     * @param voteTitle 투표 제목
     */
    @Override
    @Transactional
    public void createVoteEndedNotification(UUID userId, UUID voteId, String voteTitle) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.VOTE_CLOSED.name())
                .title("투표가 종료되었습니다")
                .content("'" + voteTitle + "' 투표가 종료되었습니다.")
                .referenceId(voteId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 새 가이드 생성 알림을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param guideId 가이드 ID
     * @param guideTitle 가이드 제목
     */
    @Override
    @Transactional
    public void createNewGuideNotification(UUID userId, UUID guideId, String guideTitle) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.NEW_GUIDE.name())
                .title("새로운 가이드가 생성되었습니다")
                .content("'" + guideTitle + "' 가이드가 새로 생성되었습니다.")
                .referenceId(guideId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
}

    /**
     * 새 댓글 알림을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param questionId 질문 ID
     * @param questionTitle 질문 제목
     */
    @Override
    @Transactional
    public void createNewCommentNotification(UUID userId, UUID questionId, String questionTitle) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.NEW_COMMENT.name())
                .title("새로운 댓글이 달렸습니다")
                .content("'" + questionTitle + "' 질문에 새로운 댓글이 달렸습니다.")
                .referenceId(questionId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Notification 엔티티를 NotificationResponse DTO로 변환합니다.
     *
     * @param notification Notification 엔티티
     * @return NotificationResponse DTO
     */
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .referenceId(notification.getReferenceId())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    // 더미 메서드 추가
    public boolean hasUnreadNotifications(UUID userId) {
        // 더미 구현: 항상 false 반환
        return false;
    }
}

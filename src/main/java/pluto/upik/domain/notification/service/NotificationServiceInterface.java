package pluto.upik.domain.notification.service;

import pluto.upik.domain.notification.data.DTO.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationServiceInterface {
    List<NotificationResponse> getUserNotifications(UUID userId);
    long getUnreadNotificationCount(UUID userId);
    NotificationResponse markAsRead(Long notificationId, UUID userId);
    int markAllAsRead(UUID userId);
    void createVoteEndedNotification(UUID userId, UUID voteId, String voteTitle);
    void createNewGuideNotification(UUID userId, UUID guideId, String guideTitle);
    void createNewCommentNotification(UUID userId, UUID questionId, String questionTitle);
}
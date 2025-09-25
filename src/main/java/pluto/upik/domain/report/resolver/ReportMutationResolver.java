package pluto.upik.domain.report.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplicationInterface;
import pluto.upik.domain.report.data.DTO.*;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

/**
 * 신고 관련 GraphQL Mutation 리졸버
 * 신고 생성, 수정, 삭제 등의 작업을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportMutationResolver {

    private final ReportApplicationInterface reportApplication;

    private final SecurityUtil securityUtil; // SecurityUtil 의존성 주입

    @RequireAuth
    @SchemaMapping(typeName = "ReportMutation", field = "rejectReport")
    public RejectReportPayload rejectReport(@Argument String targetId) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID targetUUID = UUID.fromString(targetId);

            String result = reportApplication.rejectReport(userId, targetUUID);
            log.info("신고 거부 완료 - userId: {}, targetId: {}, result: {}", userId, targetId, result);
            return new RejectReportPayload(result);
        } catch (IllegalArgumentException e) {
            log.error("신고 거부 실패 - 잘못된 UUID 형식", e);
            return new RejectReportPayload("잘못된 UUID 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("신고 거부 처리 중 오류 발생", e);
            return new RejectReportPayload("신고 거부 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "ReportMutation", field = "acceptGuideReport")
    public AcceptGuideReportResponse acceptGuideReport(@Argument String guideId) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID guideUUID = UUID.fromString(guideId);

            AcceptGuideReportRequest request = AcceptGuideReportRequest.builder()
                    .userId(userId)
                    .guideId(guideUUID)
                    .build();

            AcceptGuideReportResponse response = reportApplication.acceptGuideReport(request);
            log.info("가이드 신고 수락 완료 - userId: {}, guideId: {}, success: {}", userId, guideId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("가이드 신고 수락 실패 - 잘못된 UUID 형식", e);
            return AcceptGuideReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("가이드 신고 수락 처리 중 오류 발생", e);
            return AcceptGuideReportResponse.builder()
                    .success(false)
                    .message("가이드 신고 수락 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "ReportMutation", field = "reportQuestion")
    public QuestionReportResponse reportQuestion(
            @Argument String questionId,
            @Argument String reason) {

        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID questionUUID = UUID.fromString(questionId);

            QuestionReportRequest request = QuestionReportRequest.builder()
                    .userId(userId)
                    .questionId(questionUUID)
                    .reason(reason)
                    .build();

            QuestionReportResponse response = reportApplication.reportQuestion(request);
            log.info("질문 신고 완료 - userId: {}, questionId: {}, success: {}", userId, questionId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("질문 신고 실패 - 잘못된 UUID 형식", e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("질문 신고 처리 중 오류 발생", e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "ReportMutation", field = "rejectQuestionReport")
    public QuestionReportResponse rejectQuestionReport(
            @Argument String questionId) {

        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID questionUUID = UUID.fromString(questionId);

            RejectQuestionReportRequest request = RejectQuestionReportRequest.builder()
                    .userId(userId)
                    .questionId(questionUUID)
                    .build();

            QuestionReportResponse response = reportApplication.rejectQuestionReport(request);
            log.info("질문 신고 거부 완료 - userId: {}, questionId: {}, success: {}", userId, questionId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("질문 신고 거부 실패 - 잘못된 UUID 형식", e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("질문 신고 거부 처리 중 오류 발생", e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 거부 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "ReportMutation", field = "acceptQuestionReport")
    public QuestionReportResponse acceptQuestionReport(
            @Argument String questionId) {

        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID questionUUID = UUID.fromString(questionId);

            AcceptQuestionReportRequest request = AcceptQuestionReportRequest.builder()
                    .userId(userId)
                    .questionId(questionUUID)
                    .build();

            QuestionReportResponse response = reportApplication.acceptQuestionReport(request);
            log.info("질문 신고 수락 완료 - userId: {}, questionId: {}, success: {}", userId, questionId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("질문 신고 수락 실패 - 잘못된 UUID 형식", e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("질문 신고 수락 처리 중 오류 발생", e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 수락 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}

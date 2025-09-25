package pluto.upik.domain.report.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplicationInterface;
import pluto.upik.domain.report.data.DTO.ReportQuery;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportQueryResolver {

    private final ReportApplicationInterface reportApplication;
    private final SecurityUtil securityUtil; // SecurityUtil 의존성 주입

    /**
     * 현재 사용자가 신고한 목록을 조회합니다.
     */
    @RequireAuth
    @SchemaMapping(typeName = "ReportQuery", field = "getMyReports")
    public List<ReportResponse> getMyReports(ReportQuery parent) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            log.info("내 신고 조회 요청: userId={}", userId);

            List<ReportResponse> reports = reportApplication.getReportsByUser(userId);
            log.info("내 신고 조회 완료: userId={}, 결과 개수={}", userId, reports.size());
            return reports;
        } catch (Exception e) {
            log.warn("내 신고 조회 실패", e);
            return new ArrayList<>();
        }
    }

    /**
     * 특정 사용자가 신고한 목록을 조회합니다.
     */
    @RequireAuth
    @SchemaMapping(typeName = "ReportQuery", field = "getReportsByUser")
    public List<ReportResponse> getReportsByUser(ReportQuery parent, @Argument UUID userId) {
        try {
            log.info("사용자 신고 조회 요청: userId={}", userId);

            List<ReportResponse> reports = reportApplication.getReportsByUser(userId);
            log.info("사용자 신고 조회 완료: userId={}, 결과 개수={}", userId, reports.size());
            return reports;
        } catch (Exception e) {
            log.warn("사용자 신고 조회 실패", e);
            return new ArrayList<>();
        }
    }

    /**
     * 특정 대상에 대한 신고 목록을 조회합니다.
     */
    @RequireAuth
    @SchemaMapping(typeName = "ReportQuery", field = "getReportsByTarget")
    public List<ReportResponse> getReportsByTarget(ReportQuery parent, @Argument UUID targetId) {
        try {
            log.info("신고 대상 조회 요청: targetId={}", targetId);

            List<ReportResponse> reports = reportApplication.getReportsByTarget(targetId);
            log.info("신고 대상 조회 완료: targetId={}, 결과 개수={}", targetId, reports.size());
            return reports;
        } catch (Exception e) {
            log.warn("신고 대상 조회 실패", e);
            return new ArrayList<>();
        }
    }

    /**
     * 모든 신고 목록을 조회합니다.
     */
    @RequireAuth
    @SchemaMapping(typeName = "ReportQuery", field = "getAllReports")
    public List<ReportResponse> getAllReports(ReportQuery parent) {
        try {
            log.info("모든 신고 조회 요청");

            List<ReportResponse> reports = reportApplication.getAllReports();
            log.info("모든 신고 조회 완료: 결과 개수={}", reports.size());
            return reports;
        } catch (Exception e) {
            log.warn("모든 신고 조회 실패", e);
            return new ArrayList<>();
        }
    }
}

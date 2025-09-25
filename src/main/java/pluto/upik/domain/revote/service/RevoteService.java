
package pluto.upik.domain.revote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.revote.data.DTO.RevoteRequestInput;
import pluto.upik.domain.revote.data.DTO.RevoteRequestResponse;
import pluto.upik.domain.revote.data.model.RevoteRequest;
import pluto.upik.domain.revote.repository.RevoteRequestRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevoteService implements RevoteServiceInterface {

    private final RevoteRequestRepository revoteRequestRepository;
    private final GuideRepository guideRepository;

    @Override
    @Transactional
    public RevoteRequestResponse createRevoteRequest(UUID userId, RevoteRequestInput input) {
        // 가이드가 존재하는지 확인
        Guide guide = guideRepository.findById(input.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found"));

        // 같은 사용자가 같은 가이드에 대해 이미 요청했는지 확인
        if (revoteRequestRepository.existsByUserIdAndGuideId(userId, input.getGuideId())) {
            throw new BusinessException("You have already requested a revote for this guide");
        }

        // 재투표 요청 생성
        RevoteRequest revoteRequest = RevoteRequest.builder()
                .userId(userId)
                .guideId(input.getGuideId())
                .reason(input.getReason())
                .detailReason(input.getDetailReason())
                .build();

        RevoteRequest savedRequest = revoteRequestRepository.save(revoteRequest);

        // 가이드의 재투표 요청 수 증가
        guide.setRevoteCount(guide.getRevoteCount() + 1);
        guideRepository.save(guide);

        return RevoteRequestResponse.fromEntity(savedRequest);
    }

    @Override
    public List<RevoteRequestResponse> getRevoteRequestsByGuideId(UUID guideId) {
        return revoteRequestRepository.findByGuideIdOrderByCreatedAtDesc(guideId)
                .stream()
                .map(RevoteRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RevoteRequestResponse> getRevoteRequestsByUserId(UUID userId) {
        return revoteRequestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RevoteRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserRequestedRevote(UUID userId, UUID guideId) {
        return revoteRequestRepository.existsByUserIdAndGuideId(userId, guideId);
    }
}

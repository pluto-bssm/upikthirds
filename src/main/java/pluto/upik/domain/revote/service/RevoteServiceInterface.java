package pluto.upik.domain.revote.service;

import pluto.upik.domain.revote.data.DTO.RevoteRequestInput;
import pluto.upik.domain.revote.data.DTO.RevoteRequestResponse;

import java.util.List;
import java.util.UUID;

public interface RevoteServiceInterface {
    RevoteRequestResponse createRevoteRequest(UUID userId, RevoteRequestInput input);
    List<RevoteRequestResponse> getRevoteRequestsByGuideId(UUID guideId);
    List<RevoteRequestResponse> getRevoteRequestsByUserId(UUID userId);
    boolean hasUserRequestedRevote(UUID userId, UUID guideId);
}
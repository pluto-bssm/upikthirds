package pluto.upik.domain.search.service;

import pluto.upik.domain.search.data.DTO.SearchRequest;
import pluto.upik.domain.search.data.DTO.SearchResponse;

public interface SearchServiceInterface {
    /**
     * 사용자 검색 쿼리를 처리하여 관련 가이드 문서와 AI 요약을 제공합니다.
     * 
     * @param request 검색 요청 정보를 담은 객체
     * @return 검색 결과와 AI 요약을 포함한 응답 객체
     */
    SearchResponse search(SearchRequest request);
}
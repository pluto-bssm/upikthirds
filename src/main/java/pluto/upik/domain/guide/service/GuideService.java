package pluto.upik.domain.guide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideService {
    private final GuideRepository repository;
    public List<Guide> getAllGuide() {
        return repository.findAll();
    }
}

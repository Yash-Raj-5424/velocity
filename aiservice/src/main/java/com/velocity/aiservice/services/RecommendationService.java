package com.velocity.aiservice.services;

import com.velocity.aiservice.exception.ResourceNotFoundException;
import com.velocity.aiservice.model.Recommendation;
import com.velocity.aiservice.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getRecommendationsForUser(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getRecommendationsForActivity(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("No recommendations found for activity: " + activityId));
    }
}

package com.velocity.aiservice.services.impl;

import com.velocity.aiservice.models.Recommendation;
import com.velocity.aiservice.repositories.RecommendationRepository;
import com.velocity.aiservice.services.RecommendationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;

    @Override
    public Recommendation getRecommendationsForUser(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    @Override
    public Recommendation getRecommendationForActivity(String activityId) {
        return recommendationRepository.findByActivityId(activityId);
    }

}

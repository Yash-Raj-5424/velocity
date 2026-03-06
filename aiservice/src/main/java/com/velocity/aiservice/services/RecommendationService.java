package com.velocity.aiservice.services;

import com.velocity.aiservice.models.Recommendation;
import org.springframework.stereotype.Service;

@Service
public interface RecommendationService {

    Recommendation getRecommendationsForUser(String userId);
    Recommendation getRecommendationForActivity(String activityId);
}

package com.velocity.aiservice.services;

import com.velocity.aiservice.exception.ResourceNotFoundException;
import com.velocity.aiservice.models.Recommendation;
import com.velocity.aiservice.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getUserRecommendation(String userId){
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId){
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found for activityId: " + activityId));
    }

}

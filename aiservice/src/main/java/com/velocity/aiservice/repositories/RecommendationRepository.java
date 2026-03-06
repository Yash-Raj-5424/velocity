package com.velocity.aiservice.repositories;

import com.velocity.aiservice.models.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
        Recommendation findByUserId(String userId);
        Recommendation findByActivityId(String activityId);
}

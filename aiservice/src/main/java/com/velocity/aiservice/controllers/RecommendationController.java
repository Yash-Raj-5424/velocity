package com.velocity.aiservice.controllers;

import com.velocity.aiservice.models.Recommendation;
import com.velocity.aiservice.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Recommendation> getRecommendationsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForUser(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getRecommendationForActivity(@PathVariable String activityId) {
        return ResponseEntity.ok(recommendationService.getRecommendationForActivity(activityId));
    }

}

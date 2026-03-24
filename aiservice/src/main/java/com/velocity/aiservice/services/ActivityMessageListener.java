package com.velocity.aiservice.services;

import com.velocity.aiservice.model.Activity;
import com.velocity.aiservice.model.Recommendation;
import com.velocity.aiservice.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    public void processActivity(Activity activity) {
        log.info("Received Activity: {}" ,activity.getUserId());
        recommendationRepository.save(activityAIService.generateRecommendations(activity));
    }

}

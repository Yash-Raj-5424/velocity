package com.velocity.activityservice.services;

import com.velocity.activityservice.dto.ActivityRequest;
import com.velocity.activityservice.dto.ActivityResponse;
import com.velocity.activityservice.model.Activity;
import com.velocity.activityservice.repositories.ActivityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;

    private ActivityResponse mapToResponse(Activity activity) {

        ActivityResponse response = new ActivityResponse();
        response.setUserId(activity.getUserId());
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setMetadata(activity.getMetadata());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;


    }

    public ActivityResponse trackActivity(ActivityRequest request){

        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if(!isValidUser){
            throw new RuntimeException("Invalid user ID: " + request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .activityType(request.getActivityType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .metadata(request.getMetadata())
                .build();
        return mapToResponse(activityRepository.save(activity));
    }



}

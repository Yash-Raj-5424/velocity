package com.velocity.aiservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocity.aiservice.model.Activity;
import com.velocity.aiservice.model.Recommendation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendations(Activity activity){
        String prompt = createActivityPrompt(activity);
        String aiResponse = geminiService.generateRecommendations(prompt);
        log.info("Generated AI response for activity {}: ", aiResponse);

        return processAIResponse(aiResponse, activity);
    }

    private Recommendation processAIResponse(String aiResponse, Activity activity) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json    \\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "hearRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safetyPoints = extractSafetyPoints(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getActivityType().toString())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safetyPoints)
                    .createdAt(LocalDateTime.now())
                    .build();

        }catch (Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }

    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getActivityType().toString())
                .recommendation("Unable to generate detailed analysis at this time.")
                .improvements(Collections.singletonList("Follow your usual routines"))
                .suggestions(Collections.singletonList("Continue with your current workout plan"))
                .safety(Arrays.asList(
                        "Ensure proper warm-up and cool-down",
                        "Maintain proper hydration",
                        "Always maintain a balanced diet",
                        "Listen to your body and rest if you feel unwell"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyPoints(JsonNode safetyNode) {
        List<String> safetyList = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(point -> safetyList.add(point.asText()));
        }
        return safetyList.isEmpty() ? Collections.singletonList("No specific safety concerns identified.")
                : safetyList;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestionsList = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestionsList.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestionsList.isEmpty() ? Collections.singletonList("No specific workout suggestions identified.")
                : suggestionsList;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvementsList = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String recommendation = improvement.path("recommendation").asText();
                improvementsList.add(String.format("%s: %s", area, recommendation));
            });
        }
        return improvementsList.isEmpty() ? Collections.singletonList("No specific improvements identified.")
                : improvementsList;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(" ")
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createActivityPrompt(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getMetadata()
        );
    }
}

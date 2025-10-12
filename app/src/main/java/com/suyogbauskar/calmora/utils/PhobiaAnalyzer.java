package com.suyogbauskar.calmora.utils;

import java.util.Map;

/**
 * Utility class to analyze phobia questionnaire responses and generate insights
 */
public class PhobiaAnalyzer {

    public static class PhobiaAnalysis {
        private String phobiaType;
        private String severityLevel;
        private String phobiaDescription;
        private String physicalSymptoms;
        private String recommendedTherapy;

        public PhobiaAnalysis(String phobiaType, String severityLevel, String phobiaDescription, 
                              String physicalSymptoms, String recommendedTherapy) {
            this.phobiaType = phobiaType;
            this.severityLevel = severityLevel;
            this.phobiaDescription = phobiaDescription;
            this.physicalSymptoms = physicalSymptoms;
            this.recommendedTherapy = recommendedTherapy;
        }

        public String getPhobiaType() {
            return phobiaType;
        }

        public String getSeverityLevel() {
            return severityLevel;
        }

        public String getPhobiaDescription() {
            return phobiaDescription;
        }

        public String getPhysicalSymptoms() {
            return physicalSymptoms;
        }

        public String getRecommendedTherapy() {
            return recommendedTherapy;
        }
    }

    /**
     * Analyzes the questionnaire responses and returns a PhobiaAnalysis object
     */
    public static PhobiaAnalysis analyzeResponses(Map<String, String> responses) {
        // Determine phobia type from question 1
        String phobiaType = determinePhobiaType(responses.get("question_1"));
        
        // Determine severity level based on several factors
        String severityLevel = determineSeverityLevel(responses);
        
        // Generate description based on responses
        String phobiaDescription = generatePhobiaDescription(phobiaType, responses);
        
        // Analyze physical symptoms based on responses
        String physicalSymptoms = analyzePhysicalSymptoms(responses);
        
        // Recommend therapy based on phobia type and severity
        String recommendedTherapy = recommendTherapy(phobiaType, severityLevel, responses);
        
        return new PhobiaAnalysis(phobiaType, severityLevel, phobiaDescription, 
                                 physicalSymptoms, recommendedTherapy);
    }

    private static String determinePhobiaType(String response) {
        if (response == null) {
            return "Unspecified Phobia";
        }
        
        if (response.contains("Heights")) {
            return "Acrophobia (Fear of Heights)";
        } else if (response.contains("Enclosed spaces")) {
            return "Claustrophobia (Fear of Enclosed Spaces)";
        } else if (response.contains("Dogs")) {
            return "Cynophobia (Fear of Dogs)";
        } else if (response.contains("Water")) {
            return "Hydrophobia (Fear of Water)";
        } else if (response.contains("Spiders")) {
            return "Arachnophobia (Fear of Spiders)";
        } else {
            return "Specific Phobia";
        }
    }

    private static String determineSeverityLevel(Map<String, String> responses) {
        // Use question 3 (intensity rating) as base
        String intensityResponse = responses.get("question_3");
        
        // Default to moderate if data is missing
        if (intensityResponse == null) {
            return "Moderate";
        }
        
        // Simple mapping based on the 1-5 scale
        switch (intensityResponse) {
            case "1":
                return "Mild";
            case "2":
                return "Mild to Moderate";
            case "3":
                return "Moderate";
            case "4":
                return "Severe";
            case "5":
                return "Extreme";
            default:
                return "Moderate";
        }
    }

    private static String generatePhobiaDescription(String phobiaType, Map<String, String> responses) {
        StringBuilder description = new StringBuilder();
        
        description.append("Based on your responses, you exhibit symptoms of ");
        description.append(phobiaType);
        description.append(". ");
        
        // Add duration information
        String duration = responses.get("question_2");
        if (duration != null) {
            description.append("Your phobia appears to have been present for ");
            
            if (duration.contains("Less than 6 months")) {
                description.append("a relatively short time");
            } else if (duration.contains("6 months to 1 year")) {
                description.append("about 6 months to a year");
            } else if (duration.contains("1 to 5 years")) {
                description.append("several years");
            } else if (duration.contains("More than 5 years")) {
                description.append("a significant portion of your life");
            } else {
                description.append("some time");
            }
            description.append(" and ");
        } else {
            description.append("This phobia ");
        }
        
        // Add impact on daily life
        String impact = responses.get("question_10");
        if (impact != null) {
            if (impact.contains("No impact")) {
                description.append("does not appear to impact your daily life significantly.");
            } else if (impact.contains("Minor inconvenience")) {
                description.append("causes minor inconveniences in your daily life.");
            } else if (impact.contains("Moderate difficulty")) {
                description.append("affects your daily life moderately.");
            } else if (impact.contains("Severe disruption")) {
                description.append("severely disrupts your daily life and activities.");
            } else {
                description.append("affects your daily functioning.");
            }
        } else {
            description.append("affects your daily functioning to some degree.");
        }
        
        return description.toString();
    }

    private static String analyzePhysicalSymptoms(Map<String, String> responses) {
        StringBuilder symptoms = new StringBuilder();
        symptoms.append("You experience ");
        
        // Physical symptoms
        String physicalResponse = responses.get("question_6");
        if (physicalResponse != null) {
            if (physicalResponse.contains("No, not at all")) {
                symptoms.append("minimal physical symptoms when exposed to your fear.");
            } else if (physicalResponse.contains("Slightly")) {
                symptoms.append("mild physical symptoms such as slight nervousness when exposed to your fear.");
            } else if (physicalResponse.contains("Moderately")) {
                symptoms.append("moderate physical symptoms such as increased heart rate and mild dizziness when exposed to your fear.");
            } else if (physicalResponse.contains("Severely")) {
                symptoms.append("severe physical symptoms including rapid heartbeat, sweating, and difficulty breathing when exposed to your fear.");
            } else {
                symptoms.append("various physical symptoms when confronted with your fear.");
            }
        } else {
            symptoms.append("physical reactions when confronted with your fear.");
        }
        
        // Additional symptoms
        String additionalSymptoms = responses.get("question_8");
        if (additionalSymptoms != null && !additionalSymptoms.contains("Never")) {
            symptoms.append(" You also report ");
            if (additionalSymptoms.contains("Occasionally")) {
                symptoms.append("occasional feelings of dizziness, nausea, or trembling during exposure.");
            } else if (additionalSymptoms.contains("Almost always")) {
                symptoms.append("frequent dizziness, nausea, or trembling during exposure.");
            }
        }
        
        return symptoms.toString();
    }

    private static String recommendTherapy(String phobiaType, String severityLevel, Map<String, String> responses) {
        StringBuilder recommendation = new StringBuilder();
        
        recommendation.append("Based on your answers, we recommend ");
        
        // Recommend therapy based on severity and openness to therapy
        String openToTherapy = responses.get("question_15");
        boolean needsProfessionalHelp = severityLevel.equals("Severe") || severityLevel.equals("Extreme");
        
        if (phobiaType.contains("Acrophobia")) {
            recommendation.append("Exposure Therapy for heights. ");
            recommendation.append("This treatment gradually exposes you to various height situations in a controlled, safe environment to help reduce anxiety over time.");
        } else if (phobiaType.contains("Claustrophobia")) {
            recommendation.append("Exposure Therapy for enclosed spaces. ");
            recommendation.append("This approach helps you gradually confront your fear of confined spaces in a controlled manner, helping to reduce anxiety responses.");
        } else if (phobiaType.contains("Cynophobia")) {
            recommendation.append("Systematic Desensitization Therapy for dogs. ");
            recommendation.append("This treatment involves gradual exposure to dogs starting with images, then videos, and eventually real dogs in a controlled environment to reduce fear responses.");
        } else if (phobiaType.contains("Hydrophobia")) {
            recommendation.append("Aquatic Exposure Therapy for water fears. ");
            recommendation.append("This specialized therapy gradually introduces you to water environments, starting with shallow water and progressing to deeper water as comfort increases.");
        } else if (phobiaType.contains("Arachnophobia")) {
            recommendation.append("Graduated Exposure Therapy for spiders. ");
            recommendation.append("This treatment involves systematic desensitization starting with spider images, progressing to videos, toy spiders, and eventually live spiders in a controlled therapeutic setting.");
        } else {
            recommendation.append("Cognitive-Behavioral Therapy (CBT) combined with Exposure Therapy. ");
            recommendation.append("This combination helps you challenge negative thought patterns while gradually facing your fears in a controlled environment.");
        }
        
        // Add professional help recommendation if needed
        if (needsProfessionalHelp && openToTherapy != null && !openToTherapy.contains("No")) {
            recommendation.append(" Given the intensity of your symptoms, we also recommend consulting with a mental health professional for personalized treatment options.");
        }
        
        return recommendation.toString();
    }
}
package com.suyogbauskar.calmora.utils;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIHelper {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private String apiKey;
    private OkHttpClient client;
    private ExecutorService executor;

    public interface ChatResponseCallback {
        void onResponse(String response);
    }

    public OpenAIHelper(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void getChatResponse(String userMessage, String context, ChatResponseCallback callback) {
        executor.execute(() -> {
            try {
                String systemPrompt = buildSystemPrompt(context);
                JSONObject requestBody = buildRequestBody(systemPrompt, userMessage);
                
                Request request = new Request.Builder()
                        .url(OPENAI_API_URL)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(requestBody.toString(), JSON))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onResponse(""));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        String botResponse = parseResponse(responseBody);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onResponse(botResponse));
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResponse(""));
            }
        });
    }

    private String buildSystemPrompt(String context) {
        return "You are a helpful assistant for CalmOra, a mental wellness mobile app. " +
                "CalmOra helps users with meditation, therapy sessions, phobia analysis, and mental health tracking. " +
                "The app features include:\n" +
                "- Meditation sessions and guided breathing exercises\n" +
                "- Therapy sessions for various phobias and anxiety\n" +
                "- Phobia assessment questionnaire and analysis\n" +
                "- Progress tracking and leaderboards\n" +
                "- Music therapy and relaxation sounds\n" +
                "- User profiles and personalized recommendations\n\n" +
                "Be supportive, empathetic, and provide helpful guidance. " +
                "Keep responses concise but informative. " +
                "If users ask about features not in the app, gently redirect them to available features. " +
                "Always prioritize user mental health and suggest professional help when appropriate.\n\n" +
                "Context: " + context;
    }

    private JSONObject buildRequestBody(String systemPrompt, String userMessage) throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4.1");
        requestBody.put("max_tokens", 300);
        requestBody.put("temperature", 0.7);

        JSONArray messages = new JSONArray();
        
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.put(systemMessage);

        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", userMessage);
        messages.put(userMessageObj);

        requestBody.put("messages", messages);
        return requestBody;
    }

    private String parseResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content").trim();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}

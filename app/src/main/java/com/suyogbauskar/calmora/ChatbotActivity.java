package com.suyogbauskar.calmora;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.suyogbauskar.calmora.adapters.ChatAdapter;
import com.suyogbauskar.calmora.models.ChatMessage;
import com.suyogbauskar.calmora.utils.OpenAIHelper;
import com.suyogbauskar.calmora.utils.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private OpenAIHelper openAIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        initViews();
        setupRecyclerView();
        initFirebase();
        setupClickListeners();
        
        // Add welcome message
        addWelcomeMessage();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        progressDialog = new ProgressDialog();
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Initialize OpenAI helper and fetch API key
        fetchOpenAIKey();
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(v -> sendMessage());
        
        // Handle back button
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private void addWelcomeMessage() {
        ChatMessage welcomeMessage = new ChatMessage(
            "Hello! I'm your CalmOra assistant. I can help you with:\n\n" +
            "• Understanding app features\n" +
            "• Guidance on using meditation and therapy tools\n" +
            "• Information about your progress\n" +
            "• General wellness tips\n\n" +
            "How can I assist you today?",
            false,
            System.currentTimeMillis()
        );
        chatMessages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void fetchOpenAIKey() {
        progressDialog.show(this);
        
        db.collection("Config")
                .document("api")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressDialog.hide();
                    if (documentSnapshot.exists() && documentSnapshot.contains("openai")) {
                        String apiKey = documentSnapshot.getString("openai");
                        if (!TextUtils.isEmpty(apiKey)) {
                            openAIHelper = new OpenAIHelper(apiKey);
                        } else {
                            showError("OpenAI API key not found in Firestore");
                        }
                    } else {
                        showError("OpenAI configuration not found");
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.hide();
                    showError("Failed to fetch API configuration: " + e.getMessage());
                });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        if (openAIHelper == null) {
            showError("Chatbot is not ready. Please wait for initialization.");
            return;
        }

        // Add user message
        ChatMessage userMessage = new ChatMessage(messageText, true, System.currentTimeMillis());
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);

        // Clear input
        editTextMessage.setText("");

        // Show typing indicator
        buttonSend.setEnabled(false);
        
        // Get bot response
        getBotResponse(messageText);
    }

    private void getBotResponse(String userMessage) {
        // Get user context for personalized responses
        getUserContext(userMessage);
    }

    private void getUserContext(String userMessage) {
        if (auth.getCurrentUser() == null) {
            // If not logged in, provide general response
            openAIHelper.getChatResponse(userMessage, "", this::handleBotResponse);
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        
        // Fetch user data for context
        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    StringBuilder context = new StringBuilder();
                    context.append("User is using CalmOra, a mental wellness app with features like meditation, therapy sessions, and phobia analysis. ");
                    
                    if (userDoc.exists()) {
                        // Add user-specific context
                        if (userDoc.contains("name")) {
                            context.append("User's name: ").append(userDoc.getString("name")).append(". ");
                        }
                        
                        // Check for questionnaire responses
                        db.collection("Users")
                                .document(userId)
                                .collection("questions")
                                .document("responses")
                                .get()
                                .addOnSuccessListener(questionDoc -> {
                                    if (questionDoc.exists()) {
                                        context.append("User has completed phobia assessment questionnaire. ");
                                    }
                                    
                                    openAIHelper.getChatResponse(userMessage, context.toString(), this::handleBotResponse);
                                })
                                .addOnFailureListener(e -> {
                                    openAIHelper.getChatResponse(userMessage, context.toString(), this::handleBotResponse);
                                });
                    } else {
                        openAIHelper.getChatResponse(userMessage, context.toString(), this::handleBotResponse);
                    }
                })
                .addOnFailureListener(e -> {
                    // Fallback to general context
                    String generalContext = "User is using CalmOra, a mental wellness app with meditation and therapy features.";
                    openAIHelper.getChatResponse(userMessage, generalContext, this::handleBotResponse);
                });
    }

    private void handleBotResponse(String response) {
        runOnUiThread(() -> {
            buttonSend.setEnabled(true);
            
            if (!TextUtils.isEmpty(response)) {
                ChatMessage botMessage = new ChatMessage(response, false, System.currentTimeMillis());
                chatMessages.add(botMessage);
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
            } else {
                showError("Sorry, I couldn't process your message. Please try again.");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

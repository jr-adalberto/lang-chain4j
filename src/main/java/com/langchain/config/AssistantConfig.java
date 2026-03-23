package com.langchain.config;

import com.langchain.service.SeminovosAiService;
import com.langchain.tools.SeminovosTools;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    @Bean
    public GoogleAiGeminiChatModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiModel)
                .build();
    }

    @Bean
    public SeminovosAiService seminovosAiService(GoogleAiGeminiChatModel chatModel,
                                                 SeminovosTools seminovosTools) {
        return AiServices.builder(SeminovosAiService.class)
                .chatModel(chatModel)
                .tools(seminovosTools)
                .build();
    }
}
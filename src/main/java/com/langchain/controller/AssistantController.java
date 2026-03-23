package com.langchain.controller;


import com.langchain.service.SeminovosAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final SeminovosAiService aiService;

    public AssistantController(SeminovosAiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<AssistantResponse> ask(@RequestBody AssistantRequest request) {
        String result = aiService.handleRequest(request.message());
        return ResponseEntity.ok(new AssistantResponse(result));
    }

    public record AssistantRequest(String message) {}
    public record AssistantResponse(String response) {}
}

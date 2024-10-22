package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.service.ResponseService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ResponseService responseService;

    public ChatbotController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String response = responseService.getResponse(userMessage);
        return Map.of("response", response);
    }
}

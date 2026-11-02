package com.opspilot.api;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeChunkController {

    private final KnowledgeChunkService knowledgeChunkService;

    public KnowledgeChunkController(
            KnowledgeChunkService knowledgeChunkService) {
        this.knowledgeChunkService = knowledgeChunkService;
    }

    @PostMapping
    public Map<String, Object> saveKnowledge(
            @RequestBody Map<String, String> request) {

        String content = request.get("content");

        Long id = knowledgeChunkService.saveKnowledge(content);

        return Map.of(
                "id", id,
                "message", "Knowledge stored successfully"
        );
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchKnowledge(
            @RequestParam String question) {

        return knowledgeChunkService.searchKnowledge(question);
    }
}
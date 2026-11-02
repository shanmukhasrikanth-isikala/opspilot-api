
package com.opspilot.api;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/generate")
    public Map<String, Object> generate(
            @RequestBody Map<String, String> request) {

        List<Double> embedding =
                embeddingService.generateEmbedding(request.get("prompt"));

        return Map.of(
                "dimensions", embedding.size(),
                "embedding", embedding
        );
    }
}

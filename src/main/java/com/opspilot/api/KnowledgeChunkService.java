package com.opspilot.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KnowledgeChunkService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public KnowledgeChunkService(
            JdbcTemplate jdbcTemplate,
            EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    public Long saveKnowledge(String content) {

        // Generate embedding using Ollama
        List<Double> embedding =
                embeddingService.generateEmbedding(content);

        // Convert embedding into pgvector format
        String vector = embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // Store content and embedding in PostgreSQL
        String sql = """
                INSERT INTO knowledge_chunks (content, embedding)
                VALUES (?, ?::vector)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                content,
                vector
        );
    }

    public List<Map<String, Object>> searchKnowledge(String question) {

        List<Double> embedding =
                embeddingService.generateEmbedding(question);

        String vector = embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        String sql = """
            SELECT id, content,
                   1 - (embedding <=> ?::vector) AS similarity
            FROM knowledge_chunks
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> ?::vector
            LIMIT 3
            """;

        return jdbcTemplate.queryForList(sql, vector, vector);
    }
}
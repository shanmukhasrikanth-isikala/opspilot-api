
package com.opspilot.api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;

    public OllamaService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateAnswer(String question, String context) {

        Map<String, Object> request = Map.of(
                "model", "llama3.2",
                "messages", new Object[]{
                        Map.of(
                                "role", "system",
                                "content", """
                                    You are OpsPilot AI, an AI operations assistant.

                                    Answer questions using the provided knowledge context.
                                    If the context does not contain the answer,
                                    clearly say that you don't have enough information.
                                    Do not invent policies or facts.

                                    Knowledge context:
                                    """ + context
                        ),
                        Map.of("role", "user", "content", question)
                },
                "stream", false
        );

        Map response = restClient.post()
                .uri("/api/chat")
                .body(request)
                .retrieve()
                .body(Map.class);

        Map message = (Map) response.get("message");

        return (String) message.get("content");
    }
}

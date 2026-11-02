
package com.opspilot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private ConversationRepository conversationRepository;
    private ChatService chatService;
    private MessageRepository messageRepository;
    private OllamaService ollamaService;
    private KnowledgeChunkService knowledgeChunkService;

    @BeforeEach
    void setUp() {
        conversationRepository = mock(ConversationRepository.class);
        messageRepository = mock(MessageRepository.class);
        ollamaService = mock(OllamaService.class);
        knowledgeChunkService = mock(KnowledgeChunkService.class);

        Map<String, Object> knowledge = Map.of(
                "id", 1L,
                "content", "Customers can request refunds within 30 days.",
                "similarity", 0.799
        );

        when(knowledgeChunkService.searchKnowledge(anyString()))
                .thenReturn(List.of(knowledge));
        when(ollamaService.generateAnswer(anyString(), anyString()))
                .thenAnswer(invocation -> {
                    String question = invocation.getArgument(0);
                    String context = invocation.getArgument(1);

                    return "AI generated answer for: " + question
                            + " | Context: " + context;
                });
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        chatService = new ChatService(
                conversationRepository,
                messageRepository,
                ollamaService,
                knowledgeChunkService
        );
    }

    @Test
    void shouldStartAnswerWithExpectedPrefix() {
        String result = chatService.processQuestion("Hello");

        assertTrue(result.startsWith("AI generated answer for:"));
    }

    @Test
    void shouldReturnAnswerContainingOriginalQuestion() {
        String question = "Why are login failures happening?";

        String result = chatService.processQuestion(question);

        assertTrue(result.contains(question));
    }

    @Test
    void shouldIncludeDifferentQuestionsInAnswer() {
        String first = chatService.processQuestion("Question One");
        String second = chatService.processQuestion("Question Two");

        assertNotEquals(first, second);
    }
}

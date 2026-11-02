
package com.opspilot.api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final OllamaService ollamaService;
    private final KnowledgeChunkService knowledgeChunkService;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            OllamaService ollamaService,
            KnowledgeChunkService knowledgeChunkService
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.ollamaService = ollamaService;
        this.knowledgeChunkService = knowledgeChunkService;
    }

    @Transactional
    public String processQuestion(String question) {

        Conversation conversation =
                conversationRepository.save(new Conversation(question));

        messageRepository.save(
                new Message("USER", question, conversation));

        List<Map<String, Object>> results =
                knowledgeChunkService.searchKnowledge(question);

        String context = results.stream()
                .map(result -> (String) result.get("content"))
                .collect(Collectors.joining("\n"));

        String answer = ollamaService.generateAnswer(question, context);

        messageRepository.save(
                new Message("ASSISTANT", answer, conversation));

        return answer;
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public Optional<Conversation> getConversationById(Long id) {
        return conversationRepository.findById(id);
    }

    public Optional<List<Message>> getMessagesForConversation(Long id) {

        if (!conversationRepository.existsById(id)) {
            return Optional.empty();
        }

        return Optional.of(
                messageRepository
                        .findByConversationIdOrderByCreatedAtAsc(id));
    }
}

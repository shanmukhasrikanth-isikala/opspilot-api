
package com.opspilot.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\"}";
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@Valid @RequestBody ChatRequest request) {
        String answer = chatService.processQuestion(request.getQuestion());

        return Map.of("answer", answer);
    }

    @GetMapping("/conversations")
    public List<Conversation> getAllConversations() {
        return chatService.getAllConversations();
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<Conversation> getConversationById(
            @PathVariable Long id) {

        return chatService.getConversationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Long id) {

        return chatService.getMessagesForConversation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

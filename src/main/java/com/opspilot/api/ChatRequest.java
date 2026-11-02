
package com.opspilot.api;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {

    @NotBlank(message = "Question cannot be empty")
    private String question;

    public ChatRequest() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}

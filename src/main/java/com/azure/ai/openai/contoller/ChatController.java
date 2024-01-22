package com.azure.ai.openai.contoller;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.IterableStream;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {
    @PostMapping("/chat")
    public ChatRequestAssistantMessage getChatMessage(@RequestBody ChatRequestUserMessage userMessage) {
//process the user message and generate assistant message

        /**
         * Demonstrates how to get chat completions for the provided chat messages.
         * Completions support a wide variety of tasks and generate text that continues from or "completes" provided
         * prompt data.
         *
         * @param args Unused. Arguments to the program.
         */
        String azureOpenaiKey = "{azure openai key}";
        String endpoint = "url";
        String deploymentOrModelId = "{deploymentOrModelId}";

        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage("You are a helpful assistant. You will talk like a pirate."));
        chatMessages.add(new ChatRequestUserMessage("Can you help me?"));
        chatMessages.add(new ChatRequestAssistantMessage("Of course, me hearty! What can I do for ye?"));
        chatMessages.add(new ChatRequestUserMessage("What's the best way to train a parrot?"));

        IterableStream<ChatCompletions> chatCompletionsStream = client.getChatCompletionsStream(deploymentOrModelId,
                new ChatCompletionsOptions(chatMessages));

        // The delta is the message content for a streaming response.
        // Subsequence of streaming delta will be like:
        // "delta": {
        //     "role": "assistant"
        // },
        // "delta": {
        //     "content": "Why"
        //  },
        //  "delta": {
        //     "content": " don"
        //  },
        //  "delta": {
        //     "content": "'t"
        //  }
        chatCompletionsStream
                .stream()
                // Remove .skip(1) when using Non-Azure OpenAI API
                // Note: the first chat completions can be ignored when using Azure OpenAI service which is a known service bug.
                // TODO: remove .skip(1) when service fix the issue.
                .skip(1)
                .forEach(chatCompletions -> {
                    ChatResponseMessage delta = chatCompletions.getChoices().get(0).getDelta();
                    if (delta.getRole() != null) {
                        System.out.println("Role = " + delta.getRole());
                    }
                    if (delta.getContent() != null) {
                        System.out.print(delta.getContent());
                    }
                });
// Add a new user message
//        ChatRequestUserMessage userMessage = new ChatRequestUserMessage("What's the best way to train a parrot?");
//        chatMessages.add(userMessage);

        // Add a new assistant message
        ChatRequestAssistantMessage assistantMessage = new ChatRequestAssistantMessage("How can I help you?");
        return assistantMessage;
    }
}

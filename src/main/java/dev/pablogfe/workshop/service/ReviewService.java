package dev.pablogfe.workshop.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReviewService {

    private AzureOpenAiChatModel chatModel;
    private SyncMcpToolCallbackProvider toolCallbackProvider;
    private SystemPromptTemplate systemPromptTemplate;

    public String review (String pullrequest, String project) {
        var userMessage = new UserMessage("""
        Run review pipeline for current project and pull request.""");
        var systemMessage = systemPromptTemplate.createMessage(Map.of("pullrequest", pullrequest, "project", project));

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();

        return chatModel
                .call(new Prompt(List.of(userMessage, systemMessage), chatOptions))
                .getResult()
                .getOutput()
                .getText();
    }

    private void answerPullRequest(String pullrequest, String project) {
        var superSystemMessage = systemPromptTemplate.createMessage(Map.of("pullrequest", pullrequest, "project", project));
    }

}

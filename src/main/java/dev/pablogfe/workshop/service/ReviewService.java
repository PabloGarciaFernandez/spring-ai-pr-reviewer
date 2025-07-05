package dev.pablogfe.workshop.service;

import dev.pablogfe.workshop.tool.GitHubTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ChatModel chatModel;
    private final SystemPromptTemplate systemPromptTemplate;
    private final GitHubTools gitHubTools;

    public String review (String pullrequest, String project) {
        var userMessage = new UserMessage("""
        Run review pipeline for current project and pull request.""");
        var systemMessage = systemPromptTemplate.createMessage(Map.of("pullrequest", pullrequest, "project", project));

        return ChatClient.create(chatModel)
                .prompt(new Prompt(List.of(userMessage, systemMessage)))
                .tools(gitHubTools)
                .call()
                .content();
    }

    private void answerPullRequest(String pullrequest, String project) {
        var superSystemMessage = systemPromptTemplate.createMessage(Map.of("pullrequest", pullrequest, "project", project));
    }

}

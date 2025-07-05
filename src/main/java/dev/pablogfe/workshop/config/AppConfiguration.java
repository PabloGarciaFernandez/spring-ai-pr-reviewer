package dev.pablogfe.workshop.config;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "configuration")
public class AppConfiguration {

    @Value("classpath:system.txt")
    private Resource promptResource;

    @Setter
    @Getter
    private String jsonWebToken;

    @Setter
    @Getter
    private Long installation;

    @Bean
    public SystemPromptTemplate systemPromptTemplate() {
        return new SystemPromptTemplate(promptResource);
    }

    @Bean
    public GitHub gitHub() throws IOException {
        var gitHubApp = new GitHubBuilder().withJwtToken(jsonWebToken).build();
        var gitHubAppInstallation = gitHubApp.getApp().getInstallationById(installation);
        var installationToken = gitHubAppInstallation.createToken().create();
        return new GitHubBuilder().withAppInstallationToken(installationToken.getToken()).build();
    }

}

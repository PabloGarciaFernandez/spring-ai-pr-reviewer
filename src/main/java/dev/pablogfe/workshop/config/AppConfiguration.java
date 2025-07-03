package dev.pablogfe.workshop.config;

import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AppConfiguration {

    @Value("classpath:system.txt")
    private Resource promptResource;

    @Bean
    public SystemPromptTemplate systemPromptTemplate() {
        return new SystemPromptTemplate(promptResource);
    }

}

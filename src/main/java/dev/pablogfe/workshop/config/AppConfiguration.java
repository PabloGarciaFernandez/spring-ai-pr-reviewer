package dev.pablogfe.workshop.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "configuration")
public class AppConfiguration {

    @Value("classpath:system.txt")
    private Resource promptResource;

    @Setter
    @Getter
    private EnvironmentVariables environmentVariables;

    @Bean
    public SystemPromptTemplate systemPromptTemplate() {
        return new SystemPromptTemplate(promptResource);
    }

    @Bean
    public GitHub gitHub() throws Exception {
        var gitHubApp = new GitHubBuilder().withJwtToken(createJWT()).build();
        var gitHubAppInstallation = gitHubApp.getApp().getInstallationById(environmentVariables.getInstallation());
        var installationToken = gitHubAppInstallation.createToken().create();
        return new GitHubBuilder().withAppInstallationToken(installationToken.getToken()).build();
    }

    private RSAPrivateKey getPrivateKeyFromPEM() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(environmentVariables.getPem());

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

    private String createJWT() throws Exception {
        long now = System.currentTimeMillis();
        long exp = now + 10 * 60 * 1000;

        Algorithm algorithm = Algorithm.RSA256(null, getPrivateKeyFromPEM());

        return JWT.create()
                .withIssuer(environmentVariables.getClientId())
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(exp))
                .sign(algorithm);
    }

    @Data
    public static class EnvironmentVariables {
        private String pem;
        private Long installation;
        private String clientId;
    }

}

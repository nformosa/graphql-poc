package com.nickfish.graphqlpoc.config;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Configuration
@ConfigurationProperties(prefix = "whoiswho.security")
@Data
public class SecurityProperties {

    /**
     * Amount of hashing iterations, where formula is 2^passwordStrength iterations
     */
    private int passwordStrength;
    /**
     * Secret used to generate and verify JWT tokens
     */
    private String tokenSecret;
    /**
     * Name of the token issuer
     */
    private final String tokenIssuer = "whoiswho";
    /**
     * Duration after which a token will expire
     */
    private final Duration tokenExpiration = Duration.ofHours(4);
}

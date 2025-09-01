package pe.edu.vallegrande.projectPruebasApis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "youtube.api.enabled", havingValue = "true", matchIfMissing = false)
public class YouTubeConfig {
    // This configuration will only be active if youtube.api.enabled=true
    // Since this property doesn't exist, the YouTube components will be disabled
}
package dev.sro.gym_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import dev.sro.gym_service.config.properties.JmsProperties;
import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.config.properties.StorageProperties;

import java.util.Locale;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({
    JwtProperties.class,
    JmsProperties.class,
    StorageProperties.class
})
public class AppConfig {

    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.ENGLISH);
    }
}

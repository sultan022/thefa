package com.thefa.audit.config;

import com.thefa.audit.model.dto.rerference.CountryDTO;
import com.thefa.audit.model.entity.player.PlayerEligibility;
import com.thefa.audit.model.entity.reference.Country;
import com.thefa.common.dto.shared.Gender;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.thefa.audit.dao.repository")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@ComponentScan("com.thefa")
public class AppConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication).map(Principal::getName);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "HEAD", "PUT", "POST", "DELETE", "PATCH", "OPTIONS", "TRACE")
                        .allowedOrigins("*").allowedHeaders("*");
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper getModelMapper() {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mapper.addConverter(new AbstractConverter<LocalDate, Date>() {
            @Override
            protected Date convert(LocalDate source) {
                return source == null ? null : Date.from(source.atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
            }
        });

        mapper.addConverter(new AbstractConverter<Date, LocalDate>() {
            @Override
            protected LocalDate convert(Date source) {
                return source == null ? null :
                        (source instanceof java.sql.Date) ? ((java.sql.Date) source).toLocalDate() :
                                LocalDate.from(source.toInstant().atZone(ZoneOffset.UTC));
            }
        });

        mapper.addConverter(new AbstractConverter<PlayerEligibility, CountryDTO>() {
            @Override
            protected CountryDTO convert(PlayerEligibility source) {
                return source == null ? null : Optional.ofNullable(source.getCountry())
                        .map(country -> new CountryDTO(country.getCountryCode(), country.getCountryName(), country.getPoints(), country.getRank()))
                        .orElse(null);
            }
        });

        mapper.addConverter(new AbstractConverter<CountryDTO, PlayerEligibility>() {
            @Override
            protected PlayerEligibility convert(CountryDTO source) {
                return source == null ? null : new PlayerEligibility(new Country(source.getCountryCode()));
            }
        });

        mapper.addConverter(new AbstractConverter<String, LocalDate>() {
            @Override
            protected LocalDate convert(String source) {
                return source == null ? null : LocalDate.parse(source.substring(0, 10));
            }
        });

        mapper.addConverter(new AbstractConverter<String, Gender>() {
            @Override
            protected Gender convert(String source) {
                return source == null ? null : source.toUpperCase().startsWith("M") ? Gender.M : Gender.F;
            }
        });


        return mapper;
    }

}

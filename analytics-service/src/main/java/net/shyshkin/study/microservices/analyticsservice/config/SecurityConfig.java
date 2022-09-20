package net.shyshkin.study.microservices.analyticsservice.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.analyticsservice.security.AnalyticsUserDetailsService;
import net.shyshkin.study.microservices.analyticsservice.security.AnalyticsUserJwtConverter;
import net.shyshkin.study.microservices.analyticsservice.security.AudienceValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AnalyticsUserDetailsService userDetailsService;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf().disable();
        http
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(kafkaStreamsUserJwtConverter());

        return http.build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> kafkaStreamsUserJwtConverter() {
        return new AnalyticsUserJwtConverter(userDetailsService);
    }

    @Bean
    JwtDecoder jwtDecoder(@Qualifier("analytics-service-audience-validator") AudienceValidator audienceValidator) {
        String issuerUri = oAuth2ResourceServerProperties.getJwt().getIssuerUri();
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        DelegatingOAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(pathsToIgnore);
    }

}

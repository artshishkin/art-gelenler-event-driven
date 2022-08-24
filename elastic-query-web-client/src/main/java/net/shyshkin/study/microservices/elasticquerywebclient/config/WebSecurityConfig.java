package net.shyshkin.study.microservices.elasticquerywebclient.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests((authz) -> authz
                                .antMatchers("/").permitAll()
                                .antMatchers("/actuator/health").permitAll()
//                        .antMatchers("/**").hasRole("USER")
                                .anyRequest().fullyAuthenticated()
                );
        http.oauth2Client()
                .and()
                .oauth2Login();
        return http.build();
    }

}

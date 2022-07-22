package net.shyshkin.study.microservices.reactiveelasticqueryservice.config;

import net.shyshkin.study.microservices.config.UserConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain webFluxSecurityConfig(ServerHttpSecurity http) {
        http.authorizeExchange(authz -> authz
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/**").hasRole("USER"))
                .httpBasic()
                .and()
                .csrf().disable();
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(UserConfigData userConfigData) {
        UserDetails user = User.withUsername(userConfigData.getUsername())
                .password(passwordEncoder().encode(userConfigData.getPassword()))
                .roles(userConfigData.getRoles())
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

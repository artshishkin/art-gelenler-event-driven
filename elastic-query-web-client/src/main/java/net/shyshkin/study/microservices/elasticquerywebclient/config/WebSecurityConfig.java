package net.shyshkin.study.microservices.elasticquerywebclient.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Set;

@Slf4j
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

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {

                if (authority instanceof OidcUserAuthority) {
                    var oidcUserAuthority = (OidcUserAuthority) authority;
                    var userInfo = oidcUserAuthority.getUserInfo();

                    if (userInfo != null && userInfo.hasClaim("groups")) {
                        userInfo.getClaimAsStringList("groups")
                                .stream()
                                .map(String::toUpperCase)
                                .map("ROLE_"::concat)
                                .map(SimpleGrantedAuthority::new)
                                .forEach(mappedAuthorities::add);
                    }
                } else if (authority instanceof SimpleGrantedAuthority) {
                    mappedAuthorities.add(authority);
                }
            });
            log.debug("User has mapped authorities: {}", mappedAuthorities);
            return mappedAuthorities;
        };
    }

}

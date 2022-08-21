package net.shyshkin.study.microservices.elasticqueryservice.security;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.elasticqueryservice.Constants;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class TwitterQueryUserJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String SCOPE_CLAIM = "scope";
    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";
    private static final String DEFAULT_SCOPE_PREFIX = "SCOPE_";
    private static final String SCOPE_SEPARATOR = " ";

    private final TwitterQueryUserDetailsService userDetailsService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return Optional.of(jwt)
                .map(token -> token.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME))
                .filter(StringUtils::hasLength)
                .map(userDetailsService::loadUserByUsername)
                .filter(userDetails -> userDetails instanceof TwitterQueryUser)
                .map(userDetails -> (TwitterQueryUser) userDetails)
                .map(twitterQueryUser -> {
                    Collection<GrantedAuthority> authorities = getAuthorities(jwt);
                    twitterQueryUser.setAuthorities(authorities);
                    return new UsernamePasswordAuthenticationToken(twitterQueryUser, Constants.NA, authorities);
                })
                .orElseThrow(() -> new BadCredentialsException("User could not be found by JWT: " + jwt));
    }

    private Collection<GrantedAuthority> getAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(getRoleBasedAuthorities(jwt));
        authorities.addAll(getScopesBasedAuthorities(jwt));
        return authorities;
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> getRoleBasedAuthorities(Jwt jwt) {
        return Optional
                .ofNullable(jwt.getClaimAsMap(REALM_ACCESS_CLAIM))
                .map(rAccess -> rAccess.get(ROLES_CLAIM))
                .filter(roles -> roles instanceof Collection)
                .map(roles -> (Collection<String>) roles)
                .stream()
                .flatMap(Collection::stream)
                .map(String::toUpperCase)
                .map(DEFAULT_ROLE_PREFIX::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Collection<GrantedAuthority> getScopesBasedAuthorities(Jwt jwt) {
        String scopeField = jwt.getClaimAsString(SCOPE_CLAIM);
        return Optional
                .ofNullable(scopeField)
                .filter(Strings::isNotBlank)
                .map(scopes -> scopes.split(SCOPE_SEPARATOR))
                .stream()
                .flatMap(Stream::of)
                .filter(Strings::isNotBlank)
                .map(DEFAULT_SCOPE_PREFIX::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

}

package net.shyshkin.study.microservices.kafkastreamsservice.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.shyshkin.study.microservices.kafkastreamsservice.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

@Getter
@Builder
public class KafkaStreamsUser implements UserDetails {

    @Setter
    private Collection<? extends GrantedAuthority> authorities;
    private String username;

    private Map<String, PermissionType> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return Constants.NA;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

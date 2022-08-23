package net.shyshkin.study.microservices.elasticqueryservice.security;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.elasticqueryservice.business.QueryUserService;
import net.shyshkin.study.microservices.elasticqueryservice.transformer.UserPermissionsToUserDetailsTransformer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwitterQueryUserDetailsService implements UserDetailsService {

    private final QueryUserService queryUserService;
    private final UserPermissionsToUserDetailsTransformer transformer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return queryUserService.findAllPermissionsByUsername(username)
                .map(transformer::getUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with name: " + username));
    }
}

package net.shyshkin.study.microservices.elasticqueryservice.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticqueryservice.business.QueryUserService;
import net.shyshkin.study.microservices.elasticqueryservice.dataaccess.entity.UserPermission;
import net.shyshkin.study.microservices.elasticqueryservice.dataaccess.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterQueryUserService implements QueryUserService {

    private final UserPermissionRepository repository;

    @Override
    public Optional<List<UserPermission>> findAllPermissionsByUsername(String username) {
        log.debug("Finding all permissions of user: {}", username);
        return repository.findPermissionsByUsername(username);
    }

}

package net.shyshkin.study.microservices.elasticqueryservice.business;

import net.shyshkin.study.microservices.elasticqueryservice.dataaccess.entity.UserPermission;

import java.util.List;
import java.util.Optional;

public interface QueryUserService {

    Optional<List<UserPermission>> findAllPermissionsByUsername(String username);

}

package net.shyshkin.study.microservices.elasticqueryservice.transformer;

import net.shyshkin.study.microservices.elasticqueryservice.dataaccess.entity.UserPermission;
import net.shyshkin.study.microservices.elasticqueryservice.security.PermissionType;
import net.shyshkin.study.microservices.elasticqueryservice.security.TwitterQueryUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserPermissionsToUserDetailsTransformer {

    public TwitterQueryUser getUserDetails(List<UserPermission> userPermissions) {
        Map<String, PermissionType> permissionTypeMap = userPermissions
                .stream()
                .collect(Collectors.toMap(
                        UserPermission::getDocumentId,
                        p -> PermissionType.valueOf(p.getPermissionType())
                ));

        return TwitterQueryUser.builder()
                .username(userPermissions.get(0).getUsername())
                .permissions(permissionTypeMap)
                .build();
    }

}

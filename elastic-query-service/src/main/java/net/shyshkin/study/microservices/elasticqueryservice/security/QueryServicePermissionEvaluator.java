package net.shyshkin.study.microservices.elasticqueryservice.security;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class QueryServicePermissionEvaluator implements PermissionEvaluator {

    private static final String SUPER_USER_ROLE = "APP_SUPER_USER_ROLE";
    private final HttpServletRequest httpServletRequest;

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if (isSuperUser()) return true;

        if (targetDomainObject instanceof ElasticQueryServiceRequestModel) {
            return preAuthorize(authentication, ((ElasticQueryServiceRequestModel) targetDomainObject).getId(), permission);
        } else if (targetDomainObject == null) {
            return true;
        } else if (targetDomainObject instanceof ResponseEntity) {
            List<ElasticQueryServiceResponseModel> responseBody = ((ResponseEntity<List<ElasticQueryServiceResponseModel>>) targetDomainObject).getBody();
            Objects.requireNonNull(responseBody);
            return postAuthorize(authentication, responseBody, permission);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        if (isSuperUser()) return true;

        if (targetId == null) return false;
        return preAuthorize(authentication, (String) targetId, permission);
    }

    private boolean preAuthorize(Authentication authentication, String id, Object permission) {
        TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();
        PermissionType userPermission = twitterQueryUser.getPermissions().get(id);
        return hasPermission(userPermission, permission);
    }

    private boolean postAuthorize(Authentication authentication, List<ElasticQueryServiceResponseModel> responseBody, Object permission) {
        TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();
        for (ElasticQueryServiceResponseModel responseModel : responseBody) {
            PermissionType userPermission = twitterQueryUser.getPermissions().get(responseModel.getId());
            if (!hasPermission(userPermission, permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(PermissionType userPermission, Object requiredPermission) {
        return userPermission != null && Objects.equals(requiredPermission, userPermission.getType());
    }

    private boolean isSuperUser() {
        return httpServletRequest.isUserInRole(SUPER_USER_ROLE);
    }
}

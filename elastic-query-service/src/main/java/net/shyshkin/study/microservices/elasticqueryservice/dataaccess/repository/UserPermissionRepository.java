package net.shyshkin.study.microservices.elasticqueryservice.dataaccess.repository;

import net.shyshkin.study.microservices.elasticqueryservice.dataaccess.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UUID> {

    @Query(nativeQuery = true, value = "select p.user_permission_id as id,u.username,d.document_id,p.permission_type\n" +
            "from documents as d join user_permissions as p \n" +
            "on d.id=p.document_id \n" +
            "join users as u on u.id=p.user_id\n" +
            "where u.username = :username")
    Optional<List<UserPermission>> findPermissionsByUsername(@Param("username") String username);
}

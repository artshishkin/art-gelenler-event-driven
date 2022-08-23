package net.shyshkin.study.microservices.elasticqueryservice.dataaccess.repository;

import net.shyshkin.study.microservices.elasticqueryservice.dataaccess.entity.UserPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL95Dialect"
})
class UserPermissionRepositoryTest {

    @Autowired
    UserPermissionRepository userPermissionRepository;

    @Test
    void appUser_hasPermissionToReadOnly1Document() {
        //given
        String username = "app.user";

        //when
        Optional<List<UserPermission>> userPermissions = userPermissionRepository.findPermissionsByUsername(username);

        //then
        assertThat(userPermissions)
                .hasValueSatisfying(permList -> assertThat(permList)
                        .hasSize(1)
                        .anySatisfy(perm -> assertAll(
                                () -> assertThat(perm.getUsername()).isEqualTo(username),
                                () -> assertThat(perm.getPermissionType()).isEqualTo("READ"),
                                () -> assertThat(perm.getDocumentId()).isEqualTo("6210305696719765116")
                        ))
                );
    }

    @Test
    void appAdmin_hasPermissionToRead3Documents() {
        //given
        String username = "app.admin";

        //when
        Optional<List<UserPermission>> adminPermissions = userPermissionRepository.findPermissionsByUsername(username);

        //then
        assertThat(adminPermissions)
                .hasValueSatisfying(permList -> assertThat(permList)
                        .hasSize(3)
                        .allSatisfy(perm -> assertAll(
                                () -> assertThat(perm.getUsername()).isEqualTo(username),
                                () -> assertThat(perm.getPermissionType()).isEqualTo("READ"),
                                () -> assertThat(perm.getDocumentId()).isIn("6210305696719765116", "7836132853803420909", "2534708466246257458")
                        ))
                );
    }

    @Test
    void appSuperUser_hasPermissionToReadOnly1Document() {
        //given
        String username = "app.superuser";

        //when
        Optional<List<UserPermission>> superuserPermissions = userPermissionRepository.findPermissionsByUsername(username);

        //then
        assertThat(superuserPermissions)
                .hasValueSatisfying(permList -> assertThat(permList)
                        .hasSize(1)
                        .anySatisfy(perm -> assertAll(
                                () -> assertThat(perm.getUsername()).isEqualTo(username),
                                () -> assertThat(perm.getPermissionType()).isEqualTo("READ"),
                                () -> assertThat(perm.getDocumentId()).isEqualTo("6210305696719765116")
                        ))
                );
    }

}
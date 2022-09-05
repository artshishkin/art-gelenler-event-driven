package net.shyshkin.study.microservices.test;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import net.shyshkin.study.microservices.util.VersionUtil;
import org.apache.commons.io.FilenameUtils;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

@Testcontainers
public abstract class KeycloakAbstractTest {

    private static final String REALM_FILE_PATH = "../docker-compose/export/gelenler-tutorial-realm.json";
    private static final String DEFAULT_REALM_IMPORT_FILES_LOCATION = "/opt/keycloak/data/import/";

    protected static KeycloakContainer keycloakContainer;

    static {
        keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:" + VersionUtil.getVersion("KEYCLOAK_VERSION"))
                .withAdminUsername("admin")
                .withAdminPassword("Pa55w0rd")
                .withRealmImportFile("fake-realm.json") //fake insert to enable flag --import realm (+ withReuse() hashing workaround)
                .withCopyFileToContainer(
                        MountableFile.forHostPath(REALM_FILE_PATH),
                        DEFAULT_REALM_IMPORT_FILES_LOCATION + FilenameUtils.getName(REALM_FILE_PATH))
                .withReuse(true)
                .withStartupTimeout(Duration.ofMinutes(4));
        keycloakContainer.start();
    }

}
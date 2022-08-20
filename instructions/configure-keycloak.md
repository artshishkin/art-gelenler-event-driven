# Configure Keycloak Authorization Server

### 1. Configure Keycloak

1. Start Keycloak server
    - From folder ./docker-compose run
    - `docker-compose -f keycloak_auth_server.yml --env-file .env up -d`
2. Log in
    - [http://localhost:8081/admin](http://localhost:8081/admin)
    - Username: `admin`
    - Password: `Pa55w0rd`
3. Create realm `gelenler-tutorial`
4. Add Roles
    - Realm Roles ->
    - `app_user_role`
    - `app_admin_role`
    - `app_super_user_role`
5. Add Groups
    - Groups:
    - `app_user_group`
        - Role Mapping -> Assign Role -> `app_user_role`
    - `app_admin_group`
        - Role Mapping -> Assign Role -> `app_admin_role`
    - `app_super_user_group`
        - Role Mapping -> Assign Role -> `app_super_user_role`
6. Create Users
    - User 1:
        - Username: `app.user`
        - Email: `app.user@gmail.com`
        - First Name: `App`
        - Last Name: `User`
        - Join Group: `app_user_group`
        - Create
        - Credentials:
            - Password: `123`
            - Temporary:  OFF
            - Set password
    - User 2:
        - Username: `app.admin`
        - Email: `app.admin@gmail.com`
        - First Name: `AppKate`
        - Last Name: `Admin`
        - Join Group: `app_admin_group`
        - Create
        - Credentials:
            - Password: `234`
            - Temporary:  OFF
            - Set password
    - User 3:
        - Username: `app.superuser`
        - Email: `app.superuser@gmail.com`
        - First Name: `AppArt`
        - Last Name: `SuperUser`
        - Join Group: `app_super_user_group`
        - Create
        - Credentials:
            - Password: `345`
            - Temporary:  OFF
            - Set password
7. Create **first** client
    - Client ID: `elastic-query-web-client`
    - Client protocol: `openid-connect`
    - Save
8. Settings
    - Client authentication: `On`
    - Authorization: `Off`
    - Authentication flow:
        - Standard flow - `ON`
        - Direct Access Grants Enabled: `ON` (For simplicity of testing)
        - Service accounts roles: `ON`
    - Valid Redirect URIs (for simplicity - less secured):
        - `http://localhost:*` - for localhost testing
        - `http://host.testcontainers.internal:*` - for docker testing
        - `http://host.docker.internal:*` - for docker testing
9. Get Credentials
    - Credentials ->
    - Client Authenticator: Client Id and Secret
    - Secret: `hKeXincDbrZvb9rnoJgAAqN8YsWNQPR2`
10. Add Client scopes for `elastic-query-web-client` ( Protocol Mappers )
    - `elastic-query-web-client` -> Client scopes -> `elastic-query-web-client-dedicated` -> Mappers
    - Mapper 1
        - Configure a new mapper -> Group Membership (Mapper Type)
        - Name: `microservices-groups`
        - Token claim name: `groups`
        - Full group path: `Off`
        - Add to ID token: `On`
        - Add to access token: `On`
        - Add to userinfo: `On`
        - Save
    - Mapper 2
        - Configure a new mapper -> Audience (Mapper Type)
        - Name: `elastic-query-service`
        - Included Custom Audience: `elastic-query-service`
        - Add to ID token: `Off`
        - Add to access token: `On`
        - Save
    - Mapper 3
        - Configure a new mapper -> User Session Note (Mapper Type)
        - Name: `client-id`
        - User Session Note: `clientID`
        - Token Claim Name: `clientID`
        - Claim JSON Type: `String`
        - Add to ID token: `On`
        - Add to access token: `On`
        - Save
    - Mapper 4
        - Configure a new mapper -> User Session Note (Mapper Type)
        - Name: `client-host`
        - User Session Note: `clientHost`
        - Token Claim Name: `clientHost`
        - Claim JSON Type: `String`
        - Add to ID token: `On`
        - Add to access token: `On`
        - Save
    - Mapper 5
        - Configure a new mapper -> User Session Note (Mapper Type)
        - Name: `client-ip`
        - User Session Note: `clientIPAddress`
        - Token Claim Name: `clientIPAddress`
        - Claim JSON Type: `String`
        - Add to ID token: `On`
        - Add to access token: `On`
        - Save
11. Add client scopes for roles
    - Client scopes -> Create Client Scopes
    - Client Scope 1
        - Name: `app_user_role`
        - Save
        - Scope -> Assign role -> `app_user_role`
    - Client Scope 2
        - Name: `app_admin_role`
        - Save
        - Scope -> Assign role -> `app_admin_role`
    - Client Scope 3
        - Name: `app_super_user_role`
        - Save
        - Scope -> Assign role -> `app_super_user_role`
12. Add role related client scopes for client `elastic-query-web-client`
    - `elastic-query-web-client` -> Add client scope
    - app_user_role, app_admin_role, app_super_user_role -> Default
13. Create **second** client
    - Client ID: `elastic-query-web-client-2`
    - Client protocol: `openid-connect`
    - Save
    - Client authentication: `On`
    - Authorization: `Off`
    - Authentication flow:
        - Standard flow - `ON`
        - Direct Access Grants Enabled: `ON` (For simplicity of testing)
        - Service accounts roles: `Off`
    - Valid Redirect URIs (for simplicity - less secured):
        - `http://localhost:*` - for localhost testing
        - `http://host.testcontainers.internal:*` - for docker testing
        - `http://host.docker.internal:*` - for docker testing
    - Credentials ->
        - Client Authenticator: Client Id and Secret
        - Secret: `M7zv7wiwWhTWyeM8VnUAMDtbCpfXYPy3`
    - Mappers are the same as for client 1
14. Create **third** client
    - Client ID: `elastic-query-service`
    - Client protocol: `openid-connect`
    - Save
    - Client authentication: `On`
    - Authorization: `Off`
    - Authentication flow:
        - Standard flow - `ON`
        - Direct Access Grants Enabled: `ON` (For simplicity of testing)
        - Service accounts roles: `On`
    - Valid Redirect URIs (for simplicity - less secured):
        - `http://localhost:*` - for localhost testing
        - `http://host.testcontainers.internal:*` - for docker testing
        - `http://host.docker.internal:*` - for docker testing
    - Credentials ->
        - Client Authenticator: Client Id and Secret
        - Secret: `ev8wdPngiAmJTwIKlY94kBGC5Vxfluo7`
    - Mappers are almost the same as for client 1
    - but
    - Audience: 2 values:
      - `kafka-streams-service`
      - `analitics-service`

### 2. Export Realm

- Start exporting with compose file [keycloak-postgres-export.yml](/docker-compose/keycloak_auth_server-export.yml)
    - From folder ./docker-compose run
    - `docker-compose -f common.yml -f auth-server/keycloak-postgres-export.yml --env-file .env up -d`

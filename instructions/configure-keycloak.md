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
        - `analytics-service`

### 2. Export Realm

- Start exporting with compose file [keycloak_auth_server-export.yml](/docker-compose/keycloak_auth_server-export.yml)
    - From folder ./docker-compose run
    - `docker-compose -f common.yml -f keycloak_auth_server-export.yml --env-file .env up -d`

### 3. Import realm

- Start import with compose file [keycloak_auth_server-import.yml](/docker-compose/keycloak_auth_server-import.yml)
    - From folder ./docker-compose run
    - `docker-compose -f common.yml -f keycloak_auth_server-import.yml --env-file .env up -d`

### 4. Request Access Token - Password grant_type

1. Through IntelliJ IDEA HttpClient
    - use [1 GET Access Token - Password grant_type](/docker-compose/oauth-requests.http)
2. Through Postman or curl

```shell script
curl --location --request POST 'http://localhost:8081/realms/gelenler-tutorial/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=app.user' \
--data-urlencode 'password=123' \
--data-urlencode 'client_id=elastic-query-web-client' \
--data-urlencode 'client_secret=hKeXincDbrZvb9rnoJgAAqN8YsWNQPR2' \
--data-urlencode 'scope=openid profile'
```

- Response

```json
{
  "access_token": "eyJh...viFF8VGGQ",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbG...YSYyqqI",
  "token_type": "Bearer",
  "id_token": "eyJhb...ZAMasfZeTFA",
  "not-before-policy": 0,
  "session_state": "d89742d1-7b3a-4429-8255-dc6cfcf99511",
  "scope": "openid profile app_user_role email"
}
```

- View Access Token - [https://jwt.io](https://jwt.io)
    - "scope": "openid profile email",
    - "email_verified": false,
    - "name": "App User",
    - "preferred_username": "app.user",
    - "given_name": "App",
    - "family_name": "User",
    - "email": "app.user@gmail.com"

```json
{
  "exp": 1661066442,
  "iat": 1661066142,
  "jti": "6fed3124-e36c-4d9e-902b-4e25f1f82191",
  "iss": "http://localhost:8081/realms/gelenler-tutorial",
  "aud": [
    "elastic-query-service",
    "account"
  ],
  "sub": "ca496e25-08dd-4fef-8eaf-67d02a599807",
  "typ": "Bearer",
  "azp": "elastic-query-web-client",
  "session_state": "d89742d1-7b3a-4429-8255-dc6cfcf99511",
  "acr": "1",
  "realm_access": {
    "roles": [
      "default-roles-gelenler-tutorial",
      "app_user_role",
      "offline_access",
      "uma_authorization"
    ]
  },
  "resource_access": {
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  },
  "scope": "openid profile app_user_role email",
  "sid": "d89742d1-7b3a-4429-8255-dc6cfcf99511",
  "email_verified": false,
  "name": "App User",
  "groups": [
    "app_user_group"
  ],
  "preferred_username": "app.user",
  "given_name": "App",
  "family_name": "User",
  "email": "app.user@gmail.com"
}
```

### 5. Request Access Token - Authorization Code grant_type

- Use Browser to Get URI
    -  `http://localhost:8081/realms/gelenler-tutorial/protocol/openid-connect/auth?response_type=code&client_id=elastic-query-web-client&scope=openid profile&state=jskd879sdkj&redirect_uri=http://localhost:8083/no_matter_callback`
- Will redirect to Keycloak login page
    -  enter username and password
- Will redirect to `http://localhost:8083/no_matter_callback`
    -  `http://localhost:8083/no_matter_callback?state=jskd879sdkj&session_state=44b74481-cf7d-443a-a138-9efdcd9c4d95&code=8f6f6c61-db23-4418-9db0-7069ff07e8ff.44b74481-cf7d-443a-a138-9efdcd9c4d95.c10b3c5f-fcc4-40fd-890e-dcbd7b18e6b2`
- Copy code and make POST request
    - using IntelliJ IDEA HttpClient
        - [2 Get Access token (Authorization code Grant Type)](/docker-compose/oauth-requests.http)
    - using Postman or curl
```shell script
 curl --location --request POST 'http://localhost:8081/realms/gelenler-tutorial/protocol/openid-connect/token' \
 --header 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'grant_type=authorization_code' \
 --data-urlencode 'client_id=elastic-query-web-client' \
 --data-urlencode 'client_secret=hKeXincDbrZvb9rnoJgAAqN8YsWNQPR2' \
 --data-urlencode 'code=8f6f6c61-db23-4418-9db0-7069ff07e8ff.44b74481-cf7d-443a-a138-9efdcd9c4d95.c10b3c5f-fcc4-40fd-890e-dcbd7b18e6b2' \
 --data-urlencode 'redirect_uri=http://localhost:8083/no_matter_callback'
```
-  Will receive response
```json
{
  "access_token": "eyJhbGciO...2XVw",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhb...HyZlatxI",
  "token_type": "Bearer",
  "id_token": "eyJhbGc...APJUiZg",
  "not-before-policy": 0,
  "session_state": "b2988770-69cf-4714-bfa4-b8aef1c56bb7",
  "scope": "openid profile app_admin_role email"
}

```


package net.shyshkin.study.microservices.elasticqueryservice.security;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.config.ElasticQueryServiceConfigData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Qualifier("elastic-query-service-audience-validator")
@RequiredArgsConstructor
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final ElasticQueryServiceConfigData configData;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {

        return token.getAudience().contains(configData.getCustomAudience()) ?
                OAuth2TokenValidatorResult.success() :
                OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token",
                        "The required audience " + configData.getCustomAudience() + " is missing",
                        null
                ));
    }
}

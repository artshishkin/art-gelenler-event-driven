package net.shyshkin.study.microservices.elasticqueryservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticqueryservice.business.ElasticQueryService;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceAnalyticsResponseModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModelV2;
import net.shyshkin.study.microservices.elasticqueryservice.security.TwitterQueryUser;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
@RequiredArgsConstructor
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    @PostAuthorize("hasPermission(returnObject,'READ')")
    @Operation(summary = "Get all elastic documents")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {

        List<ElasticQueryServiceResponseModel> docs = elasticQueryService.getAllDocuments();
        log.debug("Elasticsearch returned {} documents", docs.size());
        return ResponseEntity.ok(docs);
    }

    @PreAuthorize("hasPermission(#id,'ElasticQueryServiceResponseModel','READ')")
    @Operation(summary = "Get elastic document by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/{id}")
    public ElasticQueryServiceResponseModel getDocumentById(@PathVariable String id) {
        ElasticQueryServiceResponseModel doc = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id {}", id);
        return doc;
    }

    @Operation(summary = "Get elastic document by id - version 2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ElasticQueryServiceResponseModelV2 getDocumentByIdV2(@PathVariable String id) {
        ElasticQueryServiceResponseModel doc = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id {}", id);
        return mapToV2(doc);
    }

    ElasticQueryServiceResponseModelV2 mapToV2(ElasticQueryServiceResponseModel responseModel) {
        ElasticQueryServiceResponseModelV2 v2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.valueOf(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Version 2 text: " + responseModel.getText().toUpperCase())
                .build();
        v2.add(responseModel.getLinks());
        return v2;
    }

    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
    @PostAuthorize("hasPermission(returnObject,'READ')")
    @Operation(summary = "Get elastic document by text")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/get-document-by-text")
    public ResponseEntity<ElasticQueryServiceAnalyticsResponseModel> getDocumentsByText(
            @Valid @RequestBody ElasticQueryServiceRequestModel requestModel,
            @AuthenticationPrincipal TwitterQueryUser principal,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient oAuth2AuthorizedClient) {

        log.debug("User {} is querying documents for text {}", principal.getUsername(), requestModel.getText());
        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        log.debug("Access Token: {}", accessToken);
        var analyticsResponseModel = elasticQueryService.getDocumentsByText(requestModel,
                accessToken);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Auth: {}", authentication);
        log.debug("Elasticsearch returned {} documents for text `{}`", analyticsResponseModel.getQueryResponseModels().size(), requestModel.getText());
        return ResponseEntity.ok(analyticsResponseModel);
    }

}

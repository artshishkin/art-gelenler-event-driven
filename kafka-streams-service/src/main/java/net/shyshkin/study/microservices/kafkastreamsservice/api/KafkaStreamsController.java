package net.shyshkin.study.microservices.kafkastreamsservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafkastreamsservice.model.KafkaStreamsResponseModel;
import net.shyshkin.study.microservices.kafkastreamsservice.runner.StreamsRunner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@Slf4j
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(value = "/", produces = "application/vnd.api.v1+json")
@RequiredArgsConstructor
public class KafkaStreamsController {

    private final StreamsRunner<String, Long> kafkaStreamsRunner;

    @GetMapping("/get-word-count-by-word/{word}")
    @Operation(summary = "Get word count by word.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = KafkaStreamsResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Unexpected error.")})
    public KafkaStreamsResponseModel getWordCountByWord(@PathVariable @NotEmpty String word) {
        Long count = kafkaStreamsRunner.getValueByKey(word);
        log.debug("Word count {} returned for word {}", count, word);
        return KafkaStreamsResponseModel.builder()
                .word(word)
                .wordCount(count)
                .build();
    }
}

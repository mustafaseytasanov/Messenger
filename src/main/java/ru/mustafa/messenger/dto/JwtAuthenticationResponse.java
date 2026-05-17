package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Data transfer object containing the generated JWT access token.
 *
 * @author Mustafa
 * @version 1.0
 * @param token the generated JWT access token string
 */
@Builder
@Schema(description = "Ответ c токеном доступа")
public record JwtAuthenticationResponse(
    @Schema(description = "Токен доступа", example =
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    String token
){}

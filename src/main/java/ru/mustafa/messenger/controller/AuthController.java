package ru.mustafa.messenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mustafa.messenger.dto.JwtAuthenticationResponse;
import ru.mustafa.messenger.dto.SignUpRequest;
import ru.mustafa.messenger.dto.SignInRequest;
import ru.mustafa.messenger.service.AuthenticationService;

/**
 * Controller handling user authentication and registration requests.
 *
 * @author Mustafa
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration details object
     * @return the authentication token response
     */
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    /**
     * Authenticates an existing user.
     *
     * @param request the login credentials object
     * @return the authentication token response
     */
    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
}


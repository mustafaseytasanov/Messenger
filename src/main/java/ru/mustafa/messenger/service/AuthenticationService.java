package ru.mustafa.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mustafa.messenger.dto.JwtAuthenticationResponse;
import ru.mustafa.messenger.dto.SignInRequest;
import ru.mustafa.messenger.dto.SignUpRequest;
import ru.mustafa.messenger.model.User;
import ru.mustafa.messenger.model.Role;

import java.time.LocalDateTime;

/**
 * Service handles user registration and authentication processes.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user in the system and generates a JWT token.
     *
     * @param request the registration data container
     * @return the authentication response containing the generated token
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Authenticates an existing user and generates a new JWT token.
     *
     * @param request the login credentials container
     * @return the authentication response containing the generated token
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.username());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
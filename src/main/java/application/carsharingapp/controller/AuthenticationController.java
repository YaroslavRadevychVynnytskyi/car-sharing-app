package application.carsharingapp.controller;

import application.carsharingapp.dto.user.UserLoginRequestDto;
import application.carsharingapp.dto.user.UserLoginResponseDto;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.exception.RegistrationException;
import application.carsharingapp.security.AuthenticationService;
import application.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for managing authentication")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user")
    UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Provides ability to sign in")
    UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}

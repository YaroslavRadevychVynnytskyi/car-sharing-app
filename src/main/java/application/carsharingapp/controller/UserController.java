package application.carsharingapp.controller;

import application.carsharingapp.dto.user.UpdateUserRoleRequestDto;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.model.User;
import application.carsharingapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user's role", description = "Updates user's role")
    UserResponseDto updateUserRole(@RequestBody UpdateUserRoleRequestDto requestDto,
                                   @PathVariable Long id) {
        return userService.updateUserRole(requestDto, id);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/me")
    @Operation(summary = "Get profile info", description = "Retrieves basic user info")
    UserResponseDto getProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user.getId());
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PatchMapping("/me")
    @Operation(summary = "Update profile info",
            description = "Provides possibility to change some personal data")
    UserResponseDto updateProfileInfo(Authentication authentication,
                                      @RequestBody @Valid UserRegistrationRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(user.getId(), requestDto);
    }
}

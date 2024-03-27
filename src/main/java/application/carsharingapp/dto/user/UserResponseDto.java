package application.carsharingapp.dto.user;

import application.carsharingapp.model.Role;

public record UserResponseDto(
        String email,
        String firstName,
        String lastName,
        Role.RoleName role
) {
}

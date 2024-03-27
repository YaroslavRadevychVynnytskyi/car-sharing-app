package application.carsharingapp.dto.user;

import application.carsharingapp.model.Role;

public record UpdateUserRoleRequestDto(
        Role.RoleName role
) {
}

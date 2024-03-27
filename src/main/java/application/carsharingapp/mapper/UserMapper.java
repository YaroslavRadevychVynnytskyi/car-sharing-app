package application.carsharingapp.mapper;

import application.carsharingapp.config.MapperConfig;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.model.Role;
import application.carsharingapp.model.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "role", source = "user.roles")
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserRegistrationRequestDto requestDto, @MappingTarget User user);

    default Role.RoleName getRole(Set<Role> roles) {
        return roles.stream()
                .findAny()
                .map(Role::getName)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role"));
    }
}

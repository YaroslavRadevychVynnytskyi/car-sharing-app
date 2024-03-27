package application.carsharingapp.service;

import application.carsharingapp.dto.user.UpdateUserRoleRequestDto;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto updateUserRole(UpdateUserRoleRequestDto requestDto, Long customerId);

    UserResponseDto getProfileInfo(Long userId);

    UserResponseDto updateProfileInfo(Long userId, UserRegistrationRequestDto requestDto);

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}

package application.carsharingapp.service.impl;

import application.carsharingapp.dto.user.UpdateUserRoleRequestDto;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.exception.EntityNotFoundException;
import application.carsharingapp.exception.RegistrationException;
import application.carsharingapp.mapper.UserMapper;
import application.carsharingapp.model.Role;
import application.carsharingapp.model.User;
import application.carsharingapp.repository.RoleRepository;
import application.carsharingapp.repository.UserRepository;
import application.carsharingapp.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto updateUserRole(UpdateUserRoleRequestDto requestDto, Long customerId) {
        User customer = getUserById(customerId);
        customer.setRoles(Set.of(roleRepository.getByName(requestDto.role())));
        User savedCustomer = userRepository.save(customer);
        return userMapper.toDto(savedCustomer);
    }

    @Override
    public UserResponseDto getProfileInfo(Long userId) {
        User user = getUserById(userId);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfileInfo(Long userId, UserRegistrationRequestDto requestDto) {
        User user = getUserById(userId);
        userMapper.updateUserFromDto(requestDto, user);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register. User with email "
                    + requestDto.getEmail() + " already exists!");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleRepository.getByName(Role.RoleName.CUSTOMER)));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find user with id: " + userId));
    }
}

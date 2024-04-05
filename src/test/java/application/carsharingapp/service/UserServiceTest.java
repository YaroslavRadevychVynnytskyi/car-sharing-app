package application.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import application.carsharingapp.dto.user.UpdateUserRoleRequestDto;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.exception.EntityNotFoundException;
import application.carsharingapp.exception.RegistrationException;
import application.carsharingapp.mapper.UserMapper;
import application.carsharingapp.model.Role;
import application.carsharingapp.model.User;
import application.carsharingapp.repository.role.RoleRepository;
import application.carsharingapp.repository.user.UserRepository;
import application.carsharingapp.service.user.impl.UserServiceImpl;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify the correct dto was returned "
            + "from getProfileInfo() when passed correct ID")
    void getProfileInfo_WithValidUserId_ShouldReturnCorrectUserResponseDto() {
        //Given
        Long userId = 1L;
        User user = getUserMock();
        UserResponseDto expected = getUserResponseDtoMock();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        //When
        UserResponseDto actual = userService.getProfileInfo(userId);

        //Then
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify appropriate exception was thrown due to invalid user ID input")
    public void getProfileInfo_WithInvalidUserId_ShouldThrowEntityNotFoundException() {
        //Given
        Long nonExistingId = 404L;

        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getProfileInfo(nonExistingId));

        //Then
        String expected = "Can't find user with id: " + nonExistingId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(userRepository, times(1)).findById(nonExistingId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify that UserResponseDto with updated role was returned when all ok")
    public void updateUserRole_AllOk_ShouldReturnUserResponseDtoWithUpdatedRole() {
        //Given
        Long userId = 1L;
        User user = getUserMock();

        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.MANAGER);

        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto(
                Role.RoleName.MANAGER
        );

        UserResponseDto expected = new UserResponseDto(
                "mockUser@email.com",
                "John",
                "Peters",
                Role.RoleName.MANAGER
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.getByName(requestDto.role())).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        //When
        UserResponseDto actual = userService.updateUserRole(requestDto, userId);

        //Then
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).getByName(requestDto.role());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    @DisplayName("Verify that correct UserResponseDto was returned "
            + "when passed correct RegistrationDto")
    public void register_CorrectUserRegistrationDto_ShouldReturnCorrectUserResponseDto()
            throws RegistrationException {
        //Given
        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDtoMock();

        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);

        User user = getUserMock();
        UserResponseDto expected = getUserResponseDtoMock();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(null);
        when(roleRepository.getByName(Role.RoleName.CUSTOMER)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        //When
        UserResponseDto actual = userService.register(requestDto);

        //Then
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(userMapper, times(1)).toModel(requestDto);
        verify(roleRepository, times(1)).getByName(Role.RoleName.CUSTOMER);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper, roleRepository);
    }

    private User getUserMock() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setId(1L);
        user.setEmail("mockUser@email.com");
        user.setFirstName("John");
        user.setLastName("Peters");
        user.setPassword("mockPass1234");
        user.setRoles(roles);
        user.setDeleted(false);

        return user;
    }

    private UserRegistrationRequestDto getUserRegistrationRequestDtoMock() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Peters");
        requestDto.setEmail("mockUser@email.com");
        requestDto.setPassword("mockPass1234");
        requestDto.setRepeatPassword("mockPass1234");
        return requestDto;
    }

    private UserResponseDto getUserResponseDtoMock() {
        return new UserResponseDto(
                "mockUser@email.com",
                "John",
                "Peters",
                Role.RoleName.CUSTOMER
        );
    }
}

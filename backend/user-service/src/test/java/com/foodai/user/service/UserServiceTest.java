package com.foodai.user.service;

import com.foodai.user.domain.model.*;
import com.foodai.user.domain.repository.UserRepository;
import com.foodai.user.dto.request.AddressDTO;
import com.foodai.user.dto.request.CreateUserRequest;
import com.foodai.user.dto.request.UpdateUserRequest;
import com.foodai.user.dto.response.AddressResponse;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.exception.UserAlreadyExistsException;
import com.foodai.user.exception.UserNotFoundException;
import com.foodai.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserResponse testUserResponse;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id("user-123")
            .phone("+919876543210")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .displayName("Johnny")
            .status(UserStatus.ACTIVE)
            .referralCode("FD12345678")
            .addresses(new ArrayList<>())
            .favoriteRestaurantIds(new ArrayList<>())
            .favoriteMenuItemIds(new ArrayList<>())
            .deleted(false)
            .createdAt(Instant.now())
            .build();

        testUserResponse = UserResponse.builder()
            .id("user-123")
            .phone("+919876543210")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .fullName("John Doe")
            .status(UserStatus.ACTIVE)
            .build();

        createUserRequest = CreateUserRequest.builder()
            .phone("+919876543210")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .build();
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully when valid input")
        void shouldCreateUserSuccessfully_whenValidInput() {
            // Arrange
            when(userRepository.existsByPhoneAndDeletedFalse(anyString())).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCaseAndDeletedFalse(anyString())).thenReturn(false);
            when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(testUser);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

            // Act
            UserResponse result = userService.createUser(createUserRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-123");
            assertThat(result.getPhone()).isEqualTo("+919876543210");
            assertThat(result.getFirstName()).isEqualTo("John");

            verify(userRepository).save(any(User.class));
            verify(userMapper).toEntity(createUserRequest);
            verify(userMapper).toResponse(testUser);
        }

        @Test
        @DisplayName("Should throw exception when phone already exists")
        void shouldThrowException_whenPhoneAlreadyExists() {
            // Arrange
            when(userRepository.existsByPhoneAndDeletedFalse(anyString())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("phone");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowException_whenEmailAlreadyExists() {
            // Arrange
            when(userRepository.existsByPhoneAndDeletedFalse(anyString())).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCaseAndDeletedFalse(anyString())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user when exists by ID")
        void shouldReturnUser_whenExistsById() {
            // Arrange
            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

            // Act
            UserResponse result = userService.getUserById("user-123");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-123");
            verify(userRepository).findByIdAndDeletedFalse("user-123");
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void shouldThrowException_whenUserNotFoundById() {
            // Arrange
            when(userRepository.findByIdAndDeletedFalse("unknown")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknown");
        }

        @Test
        @DisplayName("Should return user when exists by phone")
        void shouldReturnUser_whenExistsByPhone() {
            // Arrange
            when(userRepository.findByPhoneAndDeletedFalse("+919876543210")).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

            // Act
            UserResponse result = userService.getUserByPhone("+919876543210");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isEqualTo("+919876543210");
        }

        @Test
        @DisplayName("Should return user when exists by email")
        void shouldReturnUser_whenExistsByEmail() {
            // Arrange
            when(userRepository.findByEmailIgnoreCaseAndDeletedFalse("test@example.com")).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

            // Act
            UserResponse result = userService.getUserByEmail("test@example.com");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return paginated users")
        void shouldReturnPaginatedUsers() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20);
            Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);
            when(userRepository.findByDeletedFalse(pageable)).thenReturn(userPage);
            when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

            // Act
            Page<UserResponse> result = userService.getAllUsers(pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Arrange
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            doNothing().when(userMapper).updateEntityFromRequest(any(User.class), any(UpdateUserRequest.class));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

            // Act
            UserResponse result = userService.updateUser("user-123", updateRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(userRepository).save(any(User.class));
            verify(userMapper).updateEntityFromRequest(testUser, updateRequest);
        }

        @Test
        @DisplayName("Should throw exception when user not found for update")
        void shouldThrowException_whenUserNotFoundForUpdate() {
            // Arrange
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .firstName("Jane")
                .build();

            when(userRepository.findByIdAndDeletedFalse("unknown")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.updateUser("unknown", updateRequest))
                .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Arrange
            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.deleteUser("user-123", "admin");

            // Assert
            assertThat(testUser.isDeleted()).isTrue();
            assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found for delete")
        void shouldThrowException_whenUserNotFoundForDelete() {
            // Arrange
            when(userRepository.findByIdAndDeletedFalse("unknown")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.deleteUser("unknown", "admin"))
                .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Address Management Tests")
    class AddressManagementTests {

        @Test
        @DisplayName("Should add address successfully")
        void shouldAddAddressSuccessfully() {
            // Arrange
            AddressDTO addressDTO = AddressDTO.builder()
                .label("Home")
                .type(AddressType.HOME)
                .street("123 Main St")
                .city("Mumbai")
                .pincode("400001")
                .build();

            AddressVO addressVO = AddressVO.builder()
                .id("addr_123")
                .label("Home")
                .type(AddressType.HOME)
                .street("123 Main St")
                .city("Mumbai")
                .pincode("400001")
                .build();

            AddressResponse addressResponse = AddressResponse.builder()
                .id("addr_123")
                .label("Home")
                .city("Mumbai")
                .build();

            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userMapper.toAddressVO(addressDTO)).thenReturn(addressVO);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toAddressResponse(any(AddressVO.class))).thenReturn(addressResponse);

            // Act
            AddressResponse result = userService.addAddress("user-123", addressDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getLabel()).isEqualTo("Home");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should get user addresses")
        void shouldGetUserAddresses() {
            // Arrange
            AddressVO address = AddressVO.builder()
                .id("addr_123")
                .label("Home")
                .city("Mumbai")
                .build();
            testUser.setAddresses(List.of(address));

            AddressResponse addressResponse = AddressResponse.builder()
                .id("addr_123")
                .label("Home")
                .city("Mumbai")
                .build();

            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userMapper.toAddressResponseList(anyList())).thenReturn(List.of(addressResponse));

            // Act
            List<AddressResponse> result = userService.getUserAddresses("user-123");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getLabel()).isEqualTo("Home");
        }
    }

    @Nested
    @DisplayName("Favorites Tests")
    class FavoritesTests {

        @Test
        @DisplayName("Should add favorite restaurant")
        void shouldAddFavoriteRestaurant() {
            // Arrange
            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.addFavoriteRestaurant("user-123", "restaurant-456");

            // Assert
            assertThat(testUser.getFavoriteRestaurantIds()).contains("restaurant-456");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should remove favorite restaurant")
        void shouldRemoveFavoriteRestaurant() {
            // Arrange
            testUser.getFavoriteRestaurantIds().add("restaurant-456");
            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.removeFavoriteRestaurant("user-123", "restaurant-456");

            // Assert
            assertThat(testUser.getFavoriteRestaurantIds()).doesNotContain("restaurant-456");
            verify(userRepository).save(testUser);
        }
    }

    @Nested
    @DisplayName("Verification Tests")
    class VerificationTests {

        @Test
        @DisplayName("Should verify email")
        void shouldVerifyEmail() {
            // Arrange
            testUser.setStatus(UserStatus.PENDING_VERIFICATION);
            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.verifyEmail("user-123");

            // Assert
            assertThat(testUser.isEmailVerified()).isTrue();
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should verify phone and activate user")
        void shouldVerifyPhoneAndActivateUser() {
            // Arrange
            testUser.setStatus(UserStatus.PENDING_VERIFICATION);
            when(userRepository.findByIdAndDeletedFalse("user-123")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.verifyPhone("user-123");

            // Assert
            assertThat(testUser.isPhoneVerified()).isTrue();
            assertThat(testUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
            verify(userRepository).save(testUser);
        }
    }
}


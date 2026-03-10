package com.foodai.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.user.domain.model.UserStatus;
import com.foodai.user.dto.request.AddressDTO;
import com.foodai.user.dto.request.CreateUserRequest;
import com.foodai.user.dto.request.UpdateUserRequest;
import com.foodai.user.dto.response.AddressResponse;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.exception.GlobalExceptionHandler;
import com.foodai.user.exception.UserAlreadyExistsException;
import com.foodai.user.exception.UserNotFoundException;
import com.foodai.user.service.UserService;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testUserResponse = UserResponse.builder()
            .id("user-123")
            .phone("+919876543210")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .fullName("John Doe")
            .status(UserStatus.ACTIVE)
            .build();
    }

    @Nested
    @DisplayName("Create User Endpoint Tests")
    class CreateUserEndpointTests {

        @Test
        @DisplayName("Should create user and return 201")
        void shouldCreateUserAndReturn201() throws Exception {
            // Arrange
            CreateUserRequest request = CreateUserRequest.builder()
                .phone("+919876543210")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserResponse);

            // Act & Assert
            mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("user-123"))
                .andExpect(jsonPath("$.data.firstName").value("John"));

            verify(userService).createUser(any(CreateUserRequest.class));
        }

        @Test
        @DisplayName("Should return 400 for invalid request")
        void shouldReturn400ForInvalidRequest() throws Exception {
            // Arrange - missing required phone
            CreateUserRequest request = CreateUserRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .build();

            // Act & Assert
            mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when user already exists")
        void shouldReturn409WhenUserAlreadyExists() throws Exception {
            // Arrange
            CreateUserRequest request = CreateUserRequest.builder()
                .phone("+919876543210")
                .firstName("John")
                .build();

            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("phone", "+919876543210"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Get User Endpoint Tests")
    class GetUserEndpointTests {

        @Test
        @DisplayName("Should return user by ID")
        void shouldReturnUserById() throws Exception {
            // Arrange
            when(userService.getUserById("user-123")).thenReturn(testUserResponse);

            // Act & Assert
            mockMvc.perform(get("/api/v1/users/user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("user-123"));
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Arrange
            when(userService.getUserById("unknown")).thenThrow(new UserNotFoundException("unknown"));

            // Act & Assert
            mockMvc.perform(get("/api/v1/users/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should return user by phone")
        void shouldReturnUserByPhone() throws Exception {
            // Arrange
            when(userService.getUserByPhone("+919876543210")).thenReturn(testUserResponse);

            // Act & Assert
            mockMvc.perform(get("/api/v1/users/by-phone/+919876543210"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone").value("+919876543210"));
        }

        @Test
        @DisplayName("Should return user by email")
        void shouldReturnUserByEmail() throws Exception {
            // Arrange
            when(userService.getUserByEmail("test@example.com")).thenReturn(testUserResponse);

            // Act & Assert
            mockMvc.perform(get("/api/v1/users/by-email")
                    .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
        }
    }

    @Nested
    @DisplayName("Get All Users Endpoint Tests")
    class GetAllUsersEndpointTests {

        @Test
        @DisplayName("Should return paginated users")
        void shouldReturnPaginatedUsers() throws Exception {
            // Arrange
            Page<UserResponse> page = new PageImpl<>(
                List.of(testUserResponse),
                PageRequest.of(0, 20),
                1
            );
            when(userService.getAllUsers(any())).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value("user-123"));
        }
    }

    @Nested
    @DisplayName("Update User Endpoint Tests")
    class UpdateUserEndpointTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            // Arrange
            UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

            UserResponse updatedResponse = UserResponse.builder()
                .id("user-123")
                .firstName("Jane")
                .lastName("Smith")
                .build();

            when(userService.updateUser(anyString(), any(UpdateUserRequest.class))).thenReturn(updatedResponse);

            // Act & Assert
            mockMvc.perform(put("/api/v1/users/user-123")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Jane"));
        }
    }

    @Nested
    @DisplayName("Delete User Endpoint Tests")
    class DeleteUserEndpointTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {
            // Arrange
            doNothing().when(userService).deleteUser("user-123", "admin");

            // Act & Assert
            mockMvc.perform(delete("/api/v1/users/user-123")
                    .param("deletedBy", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(userService).deleteUser("user-123", "admin");
        }
    }

    @Nested
    @DisplayName("Address Endpoint Tests")
    class AddressEndpointTests {

        @Test
        @DisplayName("Should get user addresses")
        void shouldGetUserAddresses() throws Exception {
            // Arrange
            AddressResponse addressResponse = AddressResponse.builder()
                .id("addr-123")
                .label("Home")
                .city("Mumbai")
                .build();

            when(userService.getUserAddresses("user-123")).thenReturn(List.of(addressResponse));

            // Act & Assert
            mockMvc.perform(get("/api/v1/users/user-123/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].label").value("Home"));
        }

        @Test
        @DisplayName("Should add address")
        void shouldAddAddress() throws Exception {
            // Arrange
            AddressDTO request = AddressDTO.builder()
                .label("Home")
                .street("123 Main St")
                .city("Mumbai")
                .pincode("400001")
                .build();

            AddressResponse response = AddressResponse.builder()
                .id("addr-123")
                .label("Home")
                .city("Mumbai")
                .build();

            when(userService.addAddress(anyString(), any(AddressDTO.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/api/v1/users/user-123/addresses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.label").value("Home"));
        }

        @Test
        @DisplayName("Should remove address")
        void shouldRemoveAddress() throws Exception {
            // Arrange
            doNothing().when(userService).removeAddress("user-123", "addr-123");

            // Act & Assert
            mockMvc.perform(delete("/api/v1/users/user-123/addresses/addr-123"))
                .andExpect(status().isOk());

            verify(userService).removeAddress("user-123", "addr-123");
        }
    }

    @Nested
    @DisplayName("Favorites Endpoint Tests")
    class FavoritesEndpointTests {

        @Test
        @DisplayName("Should add favorite restaurant")
        void shouldAddFavoriteRestaurant() throws Exception {
            // Arrange
            doNothing().when(userService).addFavoriteRestaurant("user-123", "restaurant-456");

            // Act & Assert
            mockMvc.perform(post("/api/v1/users/user-123/favorites/restaurants/restaurant-456"))
                .andExpect(status().isOk());

            verify(userService).addFavoriteRestaurant("user-123", "restaurant-456");
        }

        @Test
        @DisplayName("Should remove favorite restaurant")
        void shouldRemoveFavoriteRestaurant() throws Exception {
            // Arrange
            doNothing().when(userService).removeFavoriteRestaurant("user-123", "restaurant-456");

            // Act & Assert
            mockMvc.perform(delete("/api/v1/users/user-123/favorites/restaurants/restaurant-456"))
                .andExpect(status().isOk());

            verify(userService).removeFavoriteRestaurant("user-123", "restaurant-456");
        }
    }

    @Nested
    @DisplayName("Verification Endpoint Tests")
    class VerificationEndpointTests {

        @Test
        @DisplayName("Should verify email")
        void shouldVerifyEmail() throws Exception {
            // Arrange
            doNothing().when(userService).verifyEmail("user-123");

            // Act & Assert
            mockMvc.perform(post("/api/v1/users/user-123/verify-email"))
                .andExpect(status().isOk());

            verify(userService).verifyEmail("user-123");
        }

        @Test
        @DisplayName("Should verify phone")
        void shouldVerifyPhone() throws Exception {
            // Arrange
            doNothing().when(userService).verifyPhone("user-123");

            // Act & Assert
            mockMvc.perform(post("/api/v1/users/user-123/verify-phone"))
                .andExpect(status().isOk());

            verify(userService).verifyPhone("user-123");
        }
    }
}


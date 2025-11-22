package ru.mirea.docsa2.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.mirea.docsa2.dto.CreateUserRequest;
import ru.mirea.docsa2.dto.UpdateUserRequest;
import ru.mirea.docsa2.dto.UserResponse;
import ru.mirea.docsa2.model.User;
import ru.mirea.docsa2.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        User user = new User(1L, "testuser", "encoded", "test@example.com");
        when(userService.getAllUsers()).thenReturn(List.of(user));

        List<UserResponse> result = userController.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).username());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        User user = new User(1L, "testuser", "encoded", "test@example.com");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().username());
    }

    @Test
    void getUserById_WhenNotExists_ShouldReturn404() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createUser_WhenValid_ShouldReturnCreatedUser() {
        CreateUserRequest request = new CreateUserRequest("testuser", "password123", "test@example.com");
        User createdUser = new User(1L, "testuser", "encoded", "test@example.com");
        
        when(userService.createUser("testuser", "password123", "test@example.com")).thenReturn(createdUser);

        ResponseEntity<UserResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().username());
    }

    @Test
    void createUser_WhenUsernameTaken_ShouldReturnBadRequest() {
        CreateUserRequest request = new CreateUserRequest("testuser", "password123", "test@example.com");
        
        when(userService.createUser("testuser", "password123", "test@example.com"))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        ResponseEntity<UserResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUser_WhenValid_ShouldReturnUpdatedUser() {
        UpdateUserRequest request = new UpdateUserRequest("newuser", null, "new@example.com");
        User updatedUser = new User(1L, "newuser", "encoded", "new@example.com");
        
        when(userService.updateUser(1L, "newuser", "new@example.com", null)).thenReturn(updatedUser);

        ResponseEntity<UserResponse> response = userController.updateUser(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().username());
    }

    @Test
    void updateUser_WhenNotExists_ShouldReturn404() {
        UpdateUserRequest request = new UpdateUserRequest("newuser", null, "new@example.com");
        
        when(userService.updateUser(1L, "newuser", "new@example.com", null))
            .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<UserResponse> response = userController.updateUser(1L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenExists_ShouldReturn204() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenNotExists_ShouldReturn404() {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}


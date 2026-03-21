package com.wesley.springboot.usuarios.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.wesley.springboot.usuarios.model.UserModel;
import com.wesley.springboot.usuarios.repository.UserRepository;
import com.wesley.springboot.usuarios.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
    }

    @Test
    void testFindById_UserExists() {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setNome("Wesley Ruan");
        user.setEmail("wesley@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserModel result = userService.findByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals("Wesley Ruan", result.getNome());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByIdOrThrow(2L));
    }
}
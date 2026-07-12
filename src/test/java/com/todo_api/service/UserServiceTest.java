package com.todo_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.todo_api.dto.RegisterRequest;
import com.todo_api.entity.User;
import com.todo_api.exception.EmailAlreadyInUseException;
import com.todo_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_encodesPasswordAndSavesUser() {
        RegisterRequest request = new RegisterRequest("Luiz", "luiz@example.com", "secret123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(request);

        assertThat(result.getName()).isEqualTo("Luiz");
        assertThat(result.getEmail()).isEqualTo("luiz@example.com");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_whenEmailAlreadyInUse_throwsExceptionAndNeverSaves() {
        RegisterRequest request = new RegisterRequest("Luiz", "luiz@example.com", "secret123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessageContaining(request.email());

        verify(userRepository, never()).save(any());
    }
}

package com.graciano.archbank.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    @Test
    @DisplayName("Should register a successful login attempt with correct data")
    void shouldRegisterSuccessfulLoginAttempt(){
        when(loginAttemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        loginAttemptService.register("test@email.com", "127.0.0.1", true);

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository, times(1)).save(captor.capture());

        LoginAttempt savedAttempt = captor.getValue();
        assertEquals("test@email.com", savedAttempt.getEmail());
        assertEquals("127.0.0.1", savedAttempt.getIpAddress());
        assertTrue(savedAttempt.getSuccess());
    }

    @Test
    @DisplayName("Should register a failed login attempt with correct data")
    void shouldRegisterFailedLoginAttempt() {
        when(loginAttemptRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        loginAttemptService.register("teste@email.com", "192.168.0.1", false);

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository, times(1)).save(captor.capture());

        LoginAttempt savedAttempt = captor.getValue();
        assertEquals("192.168.0.1", savedAttempt.getIpAddress());
        assertFalse(savedAttempt.getSuccess());
    }

}

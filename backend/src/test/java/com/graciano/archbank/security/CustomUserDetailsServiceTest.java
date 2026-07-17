package com.graciano.archbank.security;

import com.graciano.archbank.user.User;
import com.graciano.archbank.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should load user by username successfully when email exists")
    void shouldLoadUserByUsernameSuccessfully(){
        User user = buildUser();
        String email = "test@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        assertEquals(email, result.getUsername());
        assertEquals("encrypted-password", result.getPassword());
        assertTrue(result.isEnabled());
        assertFalse(result.getAuthorities().isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when email does not exist")
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist(){
        String email = "notfound@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(email));

        assertTrue(exception.getMessage().contains(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    private User buildUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .cpf("12345678910")
                .email("test@email.com")
                .phone("+53999999999")
                .password("encrypted-password")
                .transactionPin("encrypted-pin")
                .isActive(true)
                .build();
    }

}

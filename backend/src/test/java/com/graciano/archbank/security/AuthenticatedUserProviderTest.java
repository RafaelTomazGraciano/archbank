package com.graciano.archbank.security;


import com.graciano.archbank.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedUserProviderTest {

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    private final AuthenticatedUserProvider authenticatedUserProvider = new AuthenticatedUserProvider();

    @Test
    @DisplayName("Should return the authenticated user when principal is a CustomUserDetails instance")
    void shouldReturnAuthenticatedUserSuccessfully() {
        User user = User.builder()
                .name("Test")
                .cpf("12345678910")
                .email("test@email.com")
                .build();

        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUser()).thenReturn(user);

        User result = authenticatedUserProvider.getUser(authentication);

        assertEquals(user, result);
        verify(authentication).getPrincipal();
        verify(customUserDetails).getUser();
    }

    @Test
    @DisplayName("Should throw ClassCastException when principal is not a CustomUserDetails instance")
    void shouldThrowClassCastExceptionWhenPrincipalIsNotCustomUserDetails() {
        when(authentication.getPrincipal()).thenReturn("randomString");

        ClassCastException exception = assertThrows(ClassCastException.class,
                () -> authenticatedUserProvider.getUser(authentication));

        assertTrue(exception.getMessage().contains("CustomUserDetails"));
    }

}

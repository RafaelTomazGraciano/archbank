package com.graciano.archbank.auth;

import com.graciano.archbank.account.AccountService;
import com.graciano.archbank.auth.dto.LoginRequest;
import com.graciano.archbank.auth.dto.SignUpRequest;
import com.graciano.archbank.auth.dto.TokenResponse;
import com.graciano.archbank.exception.BadRequestException;
import com.graciano.archbank.exception.NotFoundException;
import com.graciano.archbank.security.CustomUserDetails;
import com.graciano.archbank.security.CustomUserDetailsService;
import com.graciano.archbank.security.JwtTokenService;
import com.graciano.archbank.user.User;
import com.graciano.archbank.user.UserRepository;
import com.graciano.archbank.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should register user, create account and return token in correct order")
    void shouldRegisterUserAndCreateAccountSuccessfully(){
        SignUpRequest request = buildRegisterRequest();
        User user = buildUser();
        UserDetails userDetails = new CustomUserDetails(user);
        String expectedToken = "fake-token-123";

        when(userService.createUser(request)).thenReturn(user);
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
        when(jwtTokenService.generateToken(userDetails)).thenReturn(expectedToken);

        TokenResponse response = authService.signUp(request);

        assertEquals(expectedToken, response.token());

        InOrder inOrder = inOrder(userService, accountService, userDetailsService, jwtTokenService);
        inOrder.verify(userService).createUser(request);
        inOrder.verify(accountService).createAccount(user);
        inOrder.verify(userDetailsService).loadUserByUsername(user.getEmail());
        inOrder.verify(jwtTokenService).generateToken(userDetails);
    }

    @Test
    @DisplayName("Should login successfully, register attempt as success and return token")
    void shouldLoginSuccessfullyAndReturnToken(){
        LoginRequest request = buildLoginRequest();
        User user = buildUser();
        UserDetails userDetails = new CustomUserDetails(user);
        String expectedToken = "fake-token-123";
        String ip = "127.0.0.1";

        when(httpRequest.getRemoteAddr()).thenReturn(ip);
        when(userDetailsService.loadUserByUsername(request.email())).thenReturn(userDetails);
        when(jwtTokenService.generateToken(userDetails)).thenReturn(expectedToken);

        TokenResponse response = authService.login(request, httpRequest);

        assertEquals(expectedToken, response.token());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertEquals(request.email(), authCaptor.getValue().getPrincipal());
        assertEquals(request.password(), authCaptor.getValue().getCredentials());

        verify(loginAttemptService).register(request.email(), ip, true);
    }

    @Test
    @DisplayName("Should throw BadRequestException and register failed attempt when credentials are invalid")
    void shouldThrowExceptionWhenCredentialsAreInvalid(){
        LoginRequest request = buildLoginRequest();
        String ip = "127.0.0.1";

        when(httpRequest.getRemoteAddr()).thenReturn(ip);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("invalid credentials"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.login(request, httpRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(loginAttemptService).register(request.email(), ip, false);
        verify(jwtTokenService, never()).generateToken(any());
    }

    private SignUpRequest buildRegisterRequest() {
        return new SignUpRequest(
                "Test",
                "12345678900",
                "test@email.com",
                "11999999999",
                "password@123",
                "1234"
        );
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

    private LoginRequest buildLoginRequest() {
        return new LoginRequest("test@email.com", "Password@123");
    }

}

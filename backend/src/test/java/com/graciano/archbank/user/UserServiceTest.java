package com.graciano.archbank.user;

import com.graciano.archbank.auth.dto.SignUpRequest;
import com.graciano.archbank.exception.BadRequestException;
import com.graciano.archbank.security.CustomUserDetails;
import com.graciano.archbank.user.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user successfully when email and CPF are unique")
    void createUserSuccessfully(){
        SignUpRequest request = buildRegisterRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encrypted-password");
        when(passwordEncoder.encode(request.transactionPin())).thenReturn("encrypted-pin");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User user = userService.createUser(request);

        assertEquals("Test", user.getName());
        assertEquals("test@email.com", user.getEmail());
        assertEquals("encrypted-password", user.getPassword());
        assertEquals("encrypted-pin", user.getTransactionPin());
        assertTrue(user.getIsActive());

        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByCpf(request.cpf());
        verify(userRepository).existsByPhone(request.phone());
        verify(passwordEncoder).encode(request.password());
        verify(passwordEncoder).encode(request.transactionPin());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(request.cpf(), savedUser.getCpf());
        assertEquals(request.phone(), savedUser.getPhone());
    }

    @Test
    @DisplayName("Should throw BadRequestException when email is already in use")
    void shouldThrowBadRequestExceptionWhenEmailAlreadyExist(){
        SignUpRequest request = buildRegisterRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(request));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).existsByCpf(any());
        verify(userRepository, never()).existsByPhone(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when CPF is already in use")
    void shouldThrowBadRequestExceptionWhenCpfAlreadyExist(){
        SignUpRequest request = buildRegisterRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByCpf(request.cpf())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(request));

        assertEquals("CPF already in use", exception.getMessage());
        verify(userRepository, never()).existsByPhone(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when phone is already in use")
    void shouldThrowBadRequestExceptionWhenPhoneAlreadyExist() {
        SignUpRequest request = buildRegisterRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(request));

        assertEquals("Phone already in use", exception.getMessage());
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByCpf(request.cpf());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Should create user successfully without checking phone uniqueness when phone is null")
    void shouldCreateUserSuccessfullyWhenPhoneIsNull() {
        SignUpRequest request = buildRegisterRequestWithoutPhone();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encrypted-password");
        when(passwordEncoder.encode(request.transactionPin())).thenReturn("encrypted-pin");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User user = userService.createUser(request);

        assertNull(user.getPhone());
        verify(userRepository, never()).existsByPhone(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return the user profile data succesfully")
    void shouldReturnUserProfileSuccessfully(){
        User user = User.builder()
                .name("Test")
                .cpf("12345678910")
                .email("test@email.com")
                .phone("+53999999999")
                .build();

        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUser()).thenReturn(user);

        UserResponse response = userService.getProfile(authentication);

        assertEquals(user.getName(), response.name());
        assertEquals(user.getCpf(), response.cpf());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getPhone(), response.phone());
        verify(authentication).getPrincipal();
        verify(customUserDetails).getUser();
    }

    @Test
    @DisplayName("Should throw ClassCastException when principal is not a CustomUserDetails instance")
    void shouldThrowClassCastExceptionWhenPrincipalIsNotCustomUserDetails(){
        when(authentication.getPrincipal()).thenReturn("randomString");

        ClassCastException exception = assertThrows(ClassCastException.class,
                () -> userService.getProfile(authentication));

        assertTrue(exception.getMessage().contains("CustomUserDetails"));
    }

    private SignUpRequest buildRegisterRequest() {
        return new SignUpRequest(
                "Test",
                "12345678910",
                "test@email.com",
                "+53999999999",
                "Password@123",
                "1234");
    }

    private SignUpRequest buildRegisterRequestWithoutPhone() {
        return new SignUpRequest(
                "Test",
                "12345678910",
                "test@email.com",
                null,
                "Password@123",
                "1234");
    }

}

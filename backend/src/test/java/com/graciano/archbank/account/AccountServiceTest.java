package com.graciano.archbank.account;

import com.graciano.archbank.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Should create account with correct defaults linked to the user")
    void shouldCreateAccountSuccessfully() {
        User user = buildUser();
        when(accountRepository.getNextSequenceValue()).thenReturn(1L);
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.createAccount(user);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals("0001", result.getBranch());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getBalance()));
        assertTrue(result.getIsActive());

        verify(accountRepository, times(1)).getNextSequenceValue();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should generate a 10 digit numeric account number")
    void shouldGenerateAccountNumberWithTenDigits() {
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(accountRepository.getNextSequenceValue()).thenReturn(1L);

        Account result = accountService.createAccount(buildUser());

        assertTrue(result.getAccountNumber().matches("\\d{10}"),
                "Account number should contain exactly 10 digits");
    }

    @Test
    @DisplayName("Should generate distinct account numbers for different sequence values")
    void shouldGenerateDistinctAccountNumbersForDifferentSequenceValues(){
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        when(accountRepository.getNextSequenceValue()).thenReturn(1L);
        Account first = accountService.createAccount(buildUser());

        when(accountRepository.getNextSequenceValue()).thenReturn(2L);
        Account second = accountService.createAccount(buildUser());

        assertNotEquals(first.getAccountNumber(), second.getAccountNumber());
    }

    @Test
    @DisplayName("Should link account to the correct user")
    void shouldLinkAccountToCorrectUser(){
        User user = buildUser();
        when(accountRepository.getNextSequenceValue()).thenReturn(1L);
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.createAccount(user);

        assertEquals(user.getEmail(), result.getUser().getEmail());
        assertEquals(user.getCpf(), result.getUser().getCpf());
    }

    private User buildUser() {
        return User.builder()
                .name("Test")
                .cpf("12345678910")
                .email("test@email.com")
                .build();
    }

}
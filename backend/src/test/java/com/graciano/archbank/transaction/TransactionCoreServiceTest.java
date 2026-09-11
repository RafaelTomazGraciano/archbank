package com.graciano.archbank.transaction;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.account.AccountRepository;
import com.graciano.archbank.exception.InsufficientBalanceException;
import com.graciano.archbank.transaction.dto.TransactionResponse;
import com.graciano.archbank.transaction.enums.TransactionStatus;
import com.graciano.archbank.transaction.enums.TransactionType;
import com.graciano.archbank.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionCoreServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionCoreService transactionCoreService;

    @Test
    @DisplayName("Should debit origin and credit destination when both accounts are provided")
    void shouldDebitOriginAndCreditDestination() {
        Account origin = buildAccount(new BigDecimal("250.00"));
        Account destination = buildAccount(new BigDecimal("100.00"));
        BigDecimal amount = new BigDecimal("50.00");

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResponse response = transactionCoreService.executeTransaction(origin, destination, amount, TransactionType.TRANSFER, "Test Transfer");

        assertEquals(0, new BigDecimal("200.00").compareTo(origin.getBalance()));
        assertEquals(0, new BigDecimal("150.00").compareTo(destination.getBalance()));
        assertEquals(TransactionStatus.COMPLETED, response.status());
        assertEquals(amount, response.amount());

        verify(accountRepository, times(1)).save(origin);
        verify(accountRepository, times(1)).save(destination);

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        assertEquals(TransactionType.TRANSFER, transactionArgumentCaptor.getValue().getType());
    }

    @Test
    @DisplayName("Should only credit destination when origin is null (deposit)")
    void shouldOnlyCreditDestinationWhenOriginIsNull() {
        Account destination = buildAccount(new BigDecimal("100.00"));
        BigDecimal amount = new BigDecimal("50.00");

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResponse response = transactionCoreService.executeTransaction(null, destination, amount, TransactionType.DEPOSIT, "Test Deposit");

        assertEquals(0, new BigDecimal("150.00").compareTo(destination.getBalance()));
        assertEquals(TransactionStatus.COMPLETED, response.status());
        assertEquals(amount, response.amount());

        verify(accountRepository, times(1)).save(destination);

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        assertEquals(TransactionType.DEPOSIT, transactionArgumentCaptor.getValue().getType());
    }

    @Test
    @DisplayName("Should only debit origin when destination is null (withdrawal)")
    void shouldOnlyDebitOriginWhenDestinationIsNull() {
        Account origin = buildAccount(new BigDecimal("100.00"));
        BigDecimal amount = new BigDecimal("50.00");

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResponse response = transactionCoreService.executeTransaction(origin, null, amount, TransactionType.WITHDRAWAL, "Test Withdrawal");

        assertEquals(0, new BigDecimal("50.00").compareTo(origin.getBalance()));
        assertEquals(TransactionStatus.COMPLETED, response.status());
        assertEquals(amount, response.amount());

        verify(accountRepository, times(1)).save(origin);

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        assertEquals(TransactionType.WITHDRAWAL, transactionArgumentCaptor.getValue().getType());
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when origin balance is lower than the amount")
    void shouldThrowInsufficientBalanceExceptionWhenBalanceIsLow() {
        Account origin = buildAccount(new BigDecimal("50.00"));
        Account destination = buildAccount(new BigDecimal("100.00"));
        BigDecimal amount = new BigDecimal("200.00");

        assertThrows(InsufficientBalanceException.class,
                () -> transactionCoreService.executeTransaction(origin, destination, amount, TransactionType.PIX, "Test Pix"));

        assertEquals(0, new BigDecimal("50.00").compareTo(origin.getBalance()));
        assertEquals(0, new BigDecimal("100.00").compareTo(destination.getBalance()));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    private Account buildAccount(BigDecimal balance) {
        return Account.builder()
                .user(User.builder().name("Test").cpf("12345678910").email("test@email.com").build())
                .accountNumber("123456789")
                .balance(balance)
                .build();
    }

}

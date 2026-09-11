package com.graciano.archbank.transaction;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.account.AccountRepository;
import com.graciano.archbank.exception.NotFoundException;
import com.graciano.archbank.exception.SelfTransferException;
import com.graciano.archbank.pix.PixKeys;
import com.graciano.archbank.pix.PixKeysRepository;
import com.graciano.archbank.security.AuthenticatedUserProvider;
import com.graciano.archbank.transaction.dto.*;
import com.graciano.archbank.transaction.enums.TransactionStatus;
import com.graciano.archbank.transaction.enums.TransactionType;
import com.graciano.archbank.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import com.graciano.archbank.pix.enums.PixKeyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PixKeysRepository pixKeysRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private TransactionCoreService transactionCoreService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should process the pix transaction successfully")
    void shouldProcessPixSuccessfully(){
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        User recipientUser = buildRecipientUser();
        Account destinationAccount = buildAccount(recipientUser, new BigDecimal("100.00"));
        PixKeys pixKeys = buildPixKeys(destinationAccount);
        BigDecimal amountToSend = new BigDecimal("50.00");
        PixRequest pixRequest = new PixRequest(PixKeyType.CPF, "123456789", amountToSend, "Test Payment");
        TransactionResponse expectedResponse = buildTransactionResponse(TransactionType.PIX, amountToSend);

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(pixKeysRepository.findByKeyTypeAndKeyValueAndIsActiveTrue(pixRequest.type(), pixRequest.recipientKey()))
                .thenReturn(Optional.of(pixKeys));
        when(transactionCoreService.executeTransaction(originAccount, destinationAccount, amountToSend, TransactionType.PIX,
                pixRequest.description())).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.processPix(pixRequest, authentication);

        assertEquals(expectedResponse, response);
        verify(accountRepository, never()).save(any());
        verify(transactionCoreService).executeTransaction(originAccount, destinationAccount, amountToSend, TransactionType.PIX, pixRequest.description());
    }

    @Test
    @DisplayName("Should throw NotFoundException when PIX key is not found")
    void shouldThrowNotFoundExceptionWhenPixKeyNotFound() {
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        PixRequest pixRequest = new PixRequest(PixKeyType.CPF, "123456789", new BigDecimal("50.00"), "Test Payment");

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(pixKeysRepository.findByKeyTypeAndKeyValueAndIsActiveTrue(pixRequest.type(), pixRequest.recipientKey()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.processPix(pixRequest, authentication));
        verify(transactionCoreService, never()).executeTransaction(any(),any(),any(),any(),any());
    }

    @Test
    @DisplayName("Should throw SelfTransferException when PIX recipient is the authenticated user's own account")
    void shouldThrowSelfTransferExceptionWhenPixToOwnAccount() {
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        PixKeys pixKeys = buildPixKeys(originAccount);
        PixRequest request = new PixRequest(PixKeyType.CPF, "12345678910", new BigDecimal("50.00"), "Test Payment");

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(pixKeysRepository.findByKeyTypeAndKeyValueAndIsActiveTrue(request.type(), request.recipientKey()))
                .thenReturn(Optional.of(pixKeys));

        assertThrows(SelfTransferException.class, () -> transactionService.processPix(request, authentication));
        verify(transactionCoreService, never()).executeTransaction(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should process a transfer successfully")
    void shouldProcessTransferSuccessfully() {
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        User recipientUser = buildRecipientUser();
        Account destinationAccount = buildAccount(recipientUser, new BigDecimal("100.00"));
        BigDecimal amountToSend = new BigDecimal("50.00");
        TransferRequest transferRequest = new TransferRequest("123456789", "0001", amountToSend, "Test Transfer");
        TransactionResponse expectedResponse = buildTransactionResponse(TransactionType.TRANSFER, amountToSend);

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumberAndBranch(transferRequest.accountNumber(), transferRequest.branch()))
                .thenReturn(Optional.of(destinationAccount));
        when(transactionCoreService.executeTransaction(originAccount, destinationAccount, amountToSend, TransactionType.TRANSFER, transferRequest.description()))
                .thenReturn(expectedResponse);

        TransactionResponse response  = transactionService.processTransfer(transferRequest, authentication);

        assertEquals(expectedResponse, response);
        verify(accountRepository, never()).save(any());
        verify(transactionCoreService).executeTransaction(originAccount, destinationAccount, amountToSend, TransactionType.TRANSFER, transferRequest.description());
    }

    @Test
    @DisplayName("Should throw NotFoundException when destination account does not exist for Transfer")
    void shouldThrowNotFoundExceptionWhenDestinationAccountNotFoundInTransfer() {
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        BigDecimal amountToSend = new BigDecimal("50.00");
        TransferRequest transferRequest = new TransferRequest("123456789", "0001", amountToSend, "Test Transfer");

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumberAndBranch(transferRequest.accountNumber(), transferRequest.branch()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.processTransfer(transferRequest, authentication));
        verify(accountRepository, never()).save(any());
        verify(transactionCoreService, never()).executeTransaction(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw SelfTransferException when transfer destination is the authenticated user's own account")
    void shouldThrowSelfTransferExceptionWhenTransferToOwnAccount() {
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        TransferRequest request = new TransferRequest("123456789", "0001", new BigDecimal("50.00"), "Test Transfer");

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumberAndBranch(request.accountNumber(), request.branch()))
                .thenReturn(Optional.of(originAccount));

        assertThrows(SelfTransferException.class, () -> transactionService.processTransfer(request, authentication));
        verify(transactionCoreService, never()).executeTransaction(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should process a deposit successfully")
    void shouldProcessDepositSuccessfully() {
        User recipientUser = buildRecipientUser();
        Account destinationAccount = buildAccount(recipientUser, new BigDecimal("100.00"));
        BigDecimal amountToSend = new BigDecimal("50.00");
        DepositRequest depositRequest = new DepositRequest("123456789", "0001", amountToSend, "Test Deposit");
        TransactionResponse expectedResponse = buildTransactionResponse(TransactionType.DEPOSIT, amountToSend);

        when(accountRepository.findByAccountNumberAndBranch(depositRequest.accountNumber(), depositRequest.branch()))
                .thenReturn(Optional.of(destinationAccount));
        when(transactionCoreService.executeTransaction(null, destinationAccount, amountToSend, TransactionType.DEPOSIT, depositRequest.description()))
                .thenReturn(expectedResponse);

        TransactionResponse response  = transactionService.processDeposit(depositRequest);

        assertEquals(expectedResponse, response);
        verify(accountRepository, never()).save(any());
        verify(transactionCoreService).executeTransaction(null, destinationAccount, amountToSend, TransactionType.DEPOSIT, depositRequest.description());
    }

    @Test
    @DisplayName("Should throw NotFoundException when destination account does not exist for Deposit")
    void shouldThrowNotFoundExceptionWhenDestinationAccountNotFoundInDeposit() {
        BigDecimal amountToSend = new BigDecimal("50.00");
        DepositRequest depositRequestRequest = new DepositRequest("123456789", "0001", amountToSend, "Test Deposit");

        when(accountRepository.findByAccountNumberAndBranch(depositRequestRequest.accountNumber(), depositRequestRequest.branch()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.processDeposit(depositRequestRequest));
        verify(accountRepository, never()).save(any());
        verify(transactionCoreService, never()).executeTransaction(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should process a withdrawal successfully")
    void shouldProcessWithdrawalSuccessfully() {
        User user = buildUser();
        Account originAccount = buildAccount(user, new BigDecimal("200.00"));
        BigDecimal amount = new BigDecimal("50.00");
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(amount, "Test Withdrawal");
        TransactionResponse expectedResponse = buildTransactionResponse(TransactionType.WITHDRAWAL, amount);

        when(authenticatedUserProvider.getUser(authentication)).thenReturn(user);
        when(accountRepository.findByUser(user)).thenReturn(originAccount);
        when(transactionCoreService.executeTransaction(originAccount, null, amount, TransactionType.WITHDRAWAL, withdrawalRequest.description()))
                .thenReturn(expectedResponse);

        TransactionResponse response = transactionService.processWithdrawal(withdrawalRequest, authentication);

        assertEquals(expectedResponse, response);
        verify(accountRepository, never()).save(any());
        verify(transactionCoreService).executeTransaction(originAccount, null, amount, TransactionType.WITHDRAWAL, withdrawalRequest.description());
    }

    private User buildUser() {
        return User.builder()
                .name("Test")
                .cpf("12345678910")
                .email("test@email.com")
                .build();
    }

    private User buildRecipientUser() {
        return User.builder()
                .name("Recipient")
                .cpf("98765432100")
                .email("recipient@email.com")
                .build();
    }

    private Account buildAccount(User user, BigDecimal balance){
        return Account.builder()
                .id(UUID.randomUUID())
                .user(user)
                .accountNumber("123456789")
                .balance(balance)
                .build();
    }

    private PixKeys buildPixKeys(Account account){
        return PixKeys.builder()
                .account(account)
                .keyType(PixKeyType.CPF)
                .keyValue("12345678910")
                .build();
    }

    private TransactionResponse buildTransactionResponse(TransactionType type, BigDecimal amount) {
        return new TransactionResponse(
                UUID.randomUUID(),
                type,
                TransactionStatus.COMPLETED,
                amount,
                "Test",
                LocalDateTime.now()
        );
    }

}

package com.graciano.archbank.transaction;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.account.AccountRepository;
import com.graciano.archbank.exception.NotFoundException;
import com.graciano.archbank.exception.SelfTransferException;
import com.graciano.archbank.pix.PixKey;
import com.graciano.archbank.pix.PixKeyRepository;
import com.graciano.archbank.security.AuthenticatedUserProvider;
import com.graciano.archbank.transaction.dto.*;
import com.graciano.archbank.transaction.enums.TransactionType;
import com.graciano.archbank.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TransactionCoreService transactionCoreService;

    @Transactional
    public TransactionResponse processPix(PixRequest request, Authentication authentication){
        Account originAccount = authenticatedUserProvider.extractAccount(authentication);

        PixKey pixKeys = pixKeyRepository.findByKeyTypeAndKeyValue(request.keyType(), request.recipientKey())
                .orElseThrow(() -> new NotFoundException("PIX key not found"));

        Account destinationAccount = pixKeys.getAccount();
        validateNotSelfTransfer(originAccount, destinationAccount);

        return transactionCoreService.executeTransaction(originAccount,destinationAccount, request.amount(), TransactionType.PIX, request.description());
    }

    @Transactional
    public TransactionResponse processTransfer(TransferRequest request, Authentication authentication){
        Account originAccount = authenticatedUserProvider.extractAccount(authentication);
        Account destinationAccount = findAccountByNumberAndBranch(request.accountNumber(), request.branch());
        validateNotSelfTransfer(originAccount, destinationAccount);

        return transactionCoreService.executeTransaction(originAccount, destinationAccount, request.amount(), TransactionType.TRANSFER, request.description());
    }

    @Transactional
    public TransactionResponse processDeposit(DepositRequest request) {
        Account destinationAccount = findAccountByNumberAndBranch(request.accountNumber(), request.branch());

        return transactionCoreService.executeTransaction(null, destinationAccount, request.amount(), TransactionType.DEPOSIT, request.description());
    }

    @Transactional
    public TransactionResponse processWithdrawal(WithdrawalRequest request, Authentication authentication) {
        Account originAccount = authenticatedUserProvider.extractAccount(authentication);

        return transactionCoreService.executeTransaction(originAccount, null, request.amount(), TransactionType.WITHDRAWAL, request.description());
    }

    private void validateNotSelfTransfer(Account origin, Account destination) {
        if (origin.getId().equals(destination.getId())) {
            throw new SelfTransferException("Cannot transfer to your own account");
        }
    }

    private Account findAccountByNumberAndBranch(String accountNumber, String branch) {
        return accountRepository.findByAccountNumberAndBranch(accountNumber, branch)
                .orElseThrow(() -> new NotFoundException("Destination account not found"));
    }
}

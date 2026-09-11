package com.graciano.archbank.transaction;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.account.AccountRepository;
import com.graciano.archbank.exception.InsufficientBalanceException;
import com.graciano.archbank.transaction.dto.TransactionResponse;
import com.graciano.archbank.transaction.enums.TransactionStatus;
import com.graciano.archbank.transaction.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TransactionCoreService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse executeTransaction(Account origin, Account destination, BigDecimal amount, TransactionType type, String description) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        if(origin != null){
            validateBalance(origin, amount);
            origin.setBalance(origin.getBalance().subtract(amount));
            accountRepository.save(origin);
        }

        if(destination != null){
            destination.setBalance(destination.getBalance().add(amount));
            accountRepository.save(destination);
        }

        Transaction transaction = Transaction.builder()
                .accountOrigin(origin)
                .accountDestination(destination)
                .amount(amount)
                .type(type)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();

        transaction = transactionRepository.save(transaction);

        return toResponse(transaction);
    }

    private void validateBalance(Account account, BigDecimal amount){
        if(account.getBalance().compareTo(amount) < 0){
            throw new InsufficientBalanceException("Insufficient balance for this transaction");
        }
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}

package com.graciano.archbank.account;

import com.graciano.archbank.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private static final long XOR_MASK = 573210947L;

    @Transactional
    public Account createAccount(User user) {
        Account account = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber())
                .build();
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccountByUser(User user){
        return accountRepository.findByUser(user);
    }

    private String generateAccountNumber() {
        Long nextId = accountRepository.getNextSequenceValue();
        long obfuscated = nextId ^ XOR_MASK;
        long nineDigitNumber = Math.abs(obfuscated) % 1_000_000_000L;
        String baseNumber = String.format("%09d", nineDigitNumber);
        int checkDigit = calculateLuhnCheckDigit(baseNumber);
        return baseNumber + checkDigit;
    }

    private int calculateLuhnCheckDigit(String numericString) {
        int runningSum = 0;
        boolean shouldDoubleDigit = true;

        for (int index = numericString.length() - 1; index >= 0; index--) {
            int currentDigit = Character.getNumericValue(numericString.charAt(index));

            if (shouldDoubleDigit) {
                currentDigit *= 2;
                if (currentDigit > 9) {
                    currentDigit = (currentDigit % 10) + 1;
                }
            }

            runningSum += currentDigit;
            shouldDoubleDigit = !shouldDoubleDigit;
        }

        int remainder = runningSum % 10;
        return (remainder == 0) ? 0 : 10 - remainder;
    }
}

package com.graciano.archbank.security;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.account.AccountService;
import com.graciano.archbank.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final AccountService accountService;

    public User getUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    public Account extractAccount(Authentication authentication){
        User user = getUser(authentication);
        return accountService.getAccountByUser(user);
    }
}
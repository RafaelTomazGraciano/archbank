package com.graciano.archbank.auth;

import com.graciano.archbank.account.AccountService;
import com.graciano.archbank.auth.dto.LoginRequest;
import com.graciano.archbank.auth.dto.RegisterRequest;
import com.graciano.archbank.auth.dto.TokenResponse;
import com.graciano.archbank.exception.BadRequestException;
import com.graciano.archbank.exception.NotFoundException;
import com.graciano.archbank.security.CustomUserDetailsService;
import com.graciano.archbank.security.JwtTokenService;
import com.graciano.archbank.user.User;
import com.graciano.archbank.user.UserRepository;
import com.graciano.archbank.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public TokenResponse register(RegisterRequest request){
        User user = userService.createUser(request);
        accountService.createAccount(user);

        String token = jwtTokenService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return new TokenResponse(token, user.getName(), user.getEmail());
    }

    public TokenResponse login(LoginRequest request, HttpServletRequest httpRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email(), request.password()));

            loginAttemptService.register(request.email(), httpRequest.getRemoteAddr(), true);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            String token = jwtTokenService.generateToken(userDetails);
            return new TokenResponse(token, user.getName(), user.getEmail());
        }catch (BadCredentialsException ex){
            loginAttemptService.register(request.email(), httpRequest.getRemoteAddr(), false);
            throw new BadRequestException("Invalid email or password");
        }
    }

}

package com.graciano.archbank.user;

import com.graciano.archbank.auth.dto.SignUpRequest;
import com.graciano.archbank.exception.BadRequestException;
import com.graciano.archbank.security.AuthenticatedUserProvider;
import com.graciano.archbank.security.CustomUserDetails;
import com.graciano.archbank.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional
    public User createUser(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByCpf(request.cpf())) {
            throw new BadRequestException("CPF already in use");
        }
        if (request.phone() != null && !request.phone().isBlank()
                && userRepository.existsByPhone(request.phone())) {
            throw new BadRequestException("Phone already in use");
        }

        User user = User.builder()
                .name(request.name())
                .cpf(request.cpf())
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .transactionPin(passwordEncoder.encode(request.transactionPin()))
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Authentication authentication){
        User user = authenticatedUserProvider.getUser(authentication);
        return new UserResponse(user.getName(), user.getCpf(), user.getEmail(), user.getPhone());
    }

}

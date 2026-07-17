package com.graciano.archbank.user;

import com.graciano.archbank.auth.dto.RegisterRequest;
import com.graciano.archbank.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByCpf(request.cpf())) {
            throw new BadRequestException("CPF already in use");
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

}

package com.graciano.archbank.pix;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.account.AccountRepository;
import com.graciano.archbank.exception.DuplicatePixKeyException;
import com.graciano.archbank.exception.NotFoundException;
import com.graciano.archbank.pix.dto.PixKeyRequest;
import com.graciano.archbank.pix.dto.PixKeyResponse;
import com.graciano.archbank.security.AuthenticatedUserProvider;
import com.graciano.archbank.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PixKeyService {

    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional
    public PixKeyResponse createPixKey(PixKeyRequest request, Authentication authentication){
        Account account = authenticatedUserProvider.extractAccount(authentication);

        if(pixKeyRepository.existsByAccountIdAndKeyType(account.getId(), request.keyType())){
            throw new DuplicatePixKeyException("You already have an active pix key of type " + request.keyType());
        }

        PixKey pixKey = PixKey.builder()
                .account(account)
                .keyType(request.keyType())
                .keyValue(request.keyValue())
                .build();

        pixKey = pixKeyRepository.save(pixKey);

        return toResponse(pixKey);
    }

    @Transactional(readOnly = true)
    public List<PixKeyResponse> getListOfMyPixKeys(Authentication authentication){
        User user = authenticatedUserProvider.getUser(authentication);

        return pixKeyRepository.findAllByAccountUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional
    public PixKeyResponse updatePixKey(UUID id, Authentication authentication){
        User user = authenticatedUserProvider.getUser(authentication);

        PixKey pixKey = pixKeyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pix key not found"));

        if(!pixKey.getAccount().getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("You are not allowed to delete this pix key");
        }

        // pode alterar apenas o valor da chave, quando for randomico
    }


    @Transactional
    public void deletePixKey(UUID id, Authentication authentication) {
        User user = authenticatedUserProvider.getUser(authentication);

        PixKey pixKey = pixKeyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pix key not found"));

        if(!pixKey.getAccount().getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("You are not allowed to delete this pix key");
        }

        pixKeyRepository.delete(pixKey);
    }

    private PixKeyResponse toResponse(PixKey pixKeys){
        return new PixKeyResponse(
                pixKeys.getId(),
                pixKeys.getKeyType(),
                pixKeys.getKeyValue(),
                pixKeys.getCreatedAt()
        );
    }

}

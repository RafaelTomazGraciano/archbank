package com.graciano.archbank.pix;

import com.graciano.archbank.pix.dto.PixKeyRequest;
import com.graciano.archbank.pix.dto.PixKeyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/pix-keys")
// ADD SWAGGER DOCS
public class PixKeyController {

    private final PixKeyService pixKeyService;

    @PostMapping
    public ResponseEntity<PixKeyResponse> createPixKey(@Valid @RequestBody PixKeyRequest request, Authentication authentication){
        PixKeyResponse response = pixKeyService.createPixKey(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PixKeyResponse>> getAllMyPixKeys(Authentication authentication){
        return ResponseEntity.ok(pixKeyService.getListOfMyPixKeys(authentication));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PixKeyResponse> updatePixKey(@PathVariable UUID id, Authentication authentication){
        return ResponseEntity.ok(pixKeyService.updatePixKey(id, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePixKey(@PathVariable UUID id, Authentication authentication){
        pixKeyService.deletePixKey(id, authentication);
        return ResponseEntity.noContent().build();
    }



}

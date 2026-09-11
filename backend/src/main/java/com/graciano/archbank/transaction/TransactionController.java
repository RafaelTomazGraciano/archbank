package com.graciano.archbank.transaction;

import com.graciano.archbank.transaction.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "PIX, transfer, deposit and withdrawal endpoints")
@RequestMapping("api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/pix")
    @Operation(summary = "Send a PIX payment",
            description = "Transfers money to a recipient identified by a PIX key (email, CPF, phone or random key)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "PIX key not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient balance")
    })
    public ResponseEntity<TransactionResponse> pix(@Valid @RequestBody PixRequest request, Authentication authentication){
        return ResponseEntity.ok(transactionService.processPix(request, authentication));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money to another account",
            description = "Transfers money to a recipient identified by account number and branch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Destination account not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient balance")
    })
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request, Authentication authentication){
        return ResponseEntity.ok(transactionService.processTransfer(request, authentication));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money into an account",
            description = "Credits the informed account number and branch with the given amount")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Destination account not found")
    })
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request){
        return ResponseEntity.ok(transactionService.processDeposit(request));
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "Withdraw money from the authenticated user's account",
            description = "Debits the authenticated user's own account with the given amount")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "422", description = "Insufficient balance")
    })
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawalRequest request, Authentication authentication){
        return ResponseEntity.ok(transactionService.processWithdrawal(request, authentication));
    }

}

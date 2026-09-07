package com.graciano.archbank.user;

import com.graciano.archbank.security.CustomUserDetails;
import com.graciano.archbank.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
@Tag(name = "User", description = "User endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user data",
            description = "Returns the profile data of the currently authenticated user, identified through the JWT token sent in the Authorization header")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired token"),
            @ApiResponse(responseCode = "403", description = "User does not have permission to access this resource")
    })
    public ResponseEntity<UserResponse> getProfile(Authentication authentication){
        UserResponse response = userService.getProfile(authentication);
        return ResponseEntity.ok(response);
    }

}

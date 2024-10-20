package com.server.auth.controller;

import com.server.auth.config.SignatureConvertor;
import com.server.auth.controller.dto.*;
import com.server.auth.domain.LoginAttempDomain;
import com.server.auth.helper.JwtHelper;
import com.server.auth.service.LoginService;
import com.server.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final LoginService loginService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.loginService = loginService;
    }

    @Operation(summary = "Signup user")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Authentication user and return token")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> signup(@Valid @RequestBody LoginRequestDto requestDto) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password()));
        } catch (BadCredentialsException e) {
            System.out.println("Message : "+e.getMessage());
            loginService.addLoginAttempts(requestDto.email(), false);
            throw e;
        }

        String privateKeyPEMString = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAMwAAAAtzc2gtZW\n" +
                "QyNTUxOQAAACBnciTw7lnYACkF31hBsIJxYvY0Hcm+XqgXPic5VaOLzAAAAKCTq71Uk6u9\n" +
                "VAAAAAtzc2gtZWQyNTUxOQAAACBnciTw7lnYACkF31hBsIJxYvY0Hcm+XqgXPic5VaOLzA\n" +
                "AAAEC9z/WXZOdpQ9qz/LJYoPMPSkfDYLi4Lgc40te2d6vnJ2dyJPDuWdgAKQXfWEGwgnFi\n" +
                "9jQdyb5eqBc+JzlVo4vMAAAAF2RhbmFrYXJATkItUTEwMzM1LmxvY2FsAQIDBAUG\n" +
                "-----END OPENSSH PRIVATE KEY-----\n";

        SignatureConvertor signatureConvertor  = new SignatureConvertor();
        PrivateKey privateKey = signatureConvertor.getPrivateKeyFromString(privateKeyPEMString);

        String token = JwtHelper.generateToken(requestDto.email(), privateKey);
        loginService.addLoginAttempts(requestDto.email(), true);
        return ResponseEntity.ok(new LoginResponseDto(requestDto.email(), token));
    }

    @Operation(summary = "Get recent login attempts")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class)))
    @GetMapping("/loginAttempts")
    public ResponseEntity<List<LoginAttemptResponseDto >> loginAttempts(@RequestHeader("Authorization") String token , @RequestHeader("publicKey") String publicKeyPEM) {
        try {
            System.out.println("loginAttempts..");
            System.out.println(token);

            // handling public key
            SignatureConvertor signatureConvertor = new SignatureConvertor();
            PublicKey publicKey = signatureConvertor.getPublicKeyFromString(publicKeyPEM);

            String email = JwtHelper.extractUsername(token.replace("Bearer ", ""), publicKey);
            List<LoginAttempDomain> loginAttempts = loginService.findRecentLoginAttempts(email);
            return ResponseEntity.ok(convertToDTOs(loginAttempts));

        } catch (BadCredentialsException e) {
            System.out.println("Message : "+e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<LoginAttemptResponseDto> convertToDTOs(List<LoginAttempDomain> loginAttempts) {
        return loginAttempts.stream()
                .map(LoginAttemptResponseDto::convertDto)
                .collect(Collectors.toList());
    }



}

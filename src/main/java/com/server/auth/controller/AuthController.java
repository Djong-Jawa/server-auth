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

        String privateKeyPEMString = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC4DDYubNQUjBV6\n" +
                "8b5S80ELdhUMTicq0BrzwpXIQ5Sygkxy9fFN6B10J68AlNNaWfwggCuoYWW2PV4s\n" +
                "je3W2H1gGQrf0KCiy7sYzjhR+IUyXgWu1awbdAh1P9KsSAxWTFvsOFEgoafBldyB\n" +
                "8AuCw7QeL9OVRETHfoTy1ayjEjIV9/cgxvLt2xwcnjP/nV31OZN0HMhcksVDantl\n" +
                "X2I4gTlR2t8Pg63KpeIcKrAAeKc/4f8d9qIlqBYL+H2sDP/pdeHyFOi4yJdnO+TY\n" +
                "4EraIlWRwZOKhYrQ9AZsJkPerfyg+1Yyv+dPVX7PRc4cNfaVdkDexbEv0BHphgGx\n" +
                "5DzteQBFAgMBAAECggEAEJk1V7mD5Ph/RW/A6O08TALRwiLYQHG1RI+jf4Ssx+6d\n" +
                "Y27P3OVWpq+fhk9lwkvEQ9AbX9us1yMwt5bzQMGUiRKpGudPXW8/R+ztQmNF6/Vz\n" +
                "0ME/fNhZnO1tHBGBX1USozqvAmDh5sETunpCJ4Br4Z8Rl3ApPc6owdfIcoqFDtr/\n" +
                "JUHfN+e7sVIMSMZvTWj9tjd/E3nf3IieCJoxq3a5Qjt/q+i19t8DRbZcx0N9a4N/\n" +
                "/y6qjzIEsp6kY1XNVITE99vfhmBh+nJ8juxOB2hmtO+JAUoI9iFeU8cOahGgslBe\n" +
                "LPMuolmHPPqQwNm9mW8YVfouB8wu9fjK/BpA5DHsgQKBgQD3xWmJ3Bd5QCROVFYt\n" +
                "xUfpn8zrgo0Nttjm26DQRQWYLFBg21pMLzuNp6YvMO5JcIL12uQ5OMarr4DnihH2\n" +
                "qkVp7Cd8URc2IY5ffGrbLuCGnqiHwOaWOJUN34VKyjn8ns0Vh4HVH607FCCRoeUn\n" +
                "siQLnFeFMO9rgolG1gab0k+3QQKBgQC+KQNXtevvOCMdkmKqtEBxqlU4hjbcwjvt\n" +
                "JXy5E8yBo3Yu95J8MpPs8xfgDMt1IkDu8Q1afIupLiw40P/LGc5dtIGfgNcGaM5U\n" +
                "giAGiitNbg1gNjRJdZkkSfQwqphOaDkEKzzgVPFJO0Bck4CVk+CLMFi7UYfZAarv\n" +
                "iBw3bNtsBQKBgAbHVuDXrai6kYO3GPh9mj8RCSk0VCmY5AxE2/x2tvV32OcdeTX2\n" +
                "YNm6SXinDQ3jTyHs0CukmwLddVhgVad69xUzyczD5y+q8Vs5ay799FSaiJ/WLjjT\n" +
                "6XgMsnfeWoHH0nKRV4L+L94a6d0yIbPZUtUifN5+/T3iv2C5wEqJEXdBAoGAJh4w\n" +
                "hld4ONam61mZn6BMPrynIpJ80tm4i3v1OWBHoUD0Aswrt+unjVrZUVcfPZalL4Bw\n" +
                "bdADauFPigATFlJQ9KWnSiLYd3dwzK3c35K4xWenFL8o0gTZstWENnEr4KOxsi08\n" +
                "obdFOqm+6A/FObaPzzbcwofAVHaqHiPJjC6s+UUCgYANOCk9Xee/lBlXpYyerIRu\n" +
                "POCk9ZDO3wXza5AcSEnv32lyAJDp6SIRlgPdQauP1/odjG4kK0cTV5hElJwo2pdd\n" +
                "QP8hNDbdJtH9X+m6MeCvZ59XixoO5XZwevuMFV4uk097AvnvkjJ81qZF9C7yHpxE\n" +
                "KIBleU2U7ISxn4twdx1zMw==" +
                "-----END PRIVATE KEY-----\n";

        SignatureConvertor signatureConvertor  = new SignatureConvertor();
        PrivateKey privateKey = signatureConvertor.getPrivateKeyFromString(privateKeyPEMString);

        String token = JwtHelper.generateToken(requestDto.email(), privateKeyPEMString);
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

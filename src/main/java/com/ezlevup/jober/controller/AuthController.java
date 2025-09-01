package com.ezlevup.jober.controller;

import com.ezlevup.jober.dto.ApiResponse;
import com.ezlevup.jober.dto.AuthResponse;
import com.ezlevup.jober.dto.LoginRequest;
import com.ezlevup.jober.dto.SignupRequest;
import com.ezlevup.jober.entity.User;
import com.ezlevup.jober.service.UserService;
import com.ezlevup.jober.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "인증", description = "사용자 인증 관련 API")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/signup")
    @Operation(
        summary = "회원가입",
        description = "새로운 사용자 계정을 생성합니다. 이메일, 비밀번호, 닉네임이 필요합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 정보",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SignupRequest.class),
                examples = @ExampleObject(
                    name = "회원가입 예제",
                    value = "{\"email\":\"user@example.com\",\"password\":\"password123\",\"nickname\":\"사용자\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = "{\"success\":true,\"message\":\"회원가입이 완료되었습니다.\",\"data\":{\"token\":\"jwt-token\",\"user\":{\"id\":1,\"email\":\"user@example.com\",\"nickname\":\"사용자\"}}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효성 검증 실패)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "실패 응답",
                    value = "{\"success\":false,\"message\":\"입력값에 오류가 있습니다.\",\"fieldErrors\":{\"email\":\"이메일은 필수입니다.\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> signup(
        @Parameter(description = "회원가입 요청 정보", required = true)
        @RequestBody SignupRequest request) {
        try {
            Map<String, String> validationErrors = userService.validateSignupRequest(request);
            
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("입력값에 오류가 있습니다.", validationErrors));
            }
            
            User user = userService.createUser(request);
            
            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            
            User responseUser = new User();
            responseUser.setId(user.getId());
            responseUser.setEmail(user.getEmail());
            responseUser.setNickname(user.getNickname());
            responseUser.setCreatedAt(user.getCreatedAt());
            
            AuthResponse authResponse = new AuthResponse(token, responseUser);
            
            return ResponseEntity.ok(
                ApiResponse.success("회원가입이 완료되었습니다.", authResponse)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("회원가입 처리 중 오류가 발생했습니다."));
        }
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "사용자 계정으로 로그인하여 JWT 토큰을 발급받습니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인 정보",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "로그인 예제",
                    value = "{\"email\":\"user@example.com\",\"password\":\"password123\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = "{\"success\":true,\"message\":\"로그인이 완료되었습니다.\",\"data\":{\"token\":\"jwt-token\",\"user\":{\"id\":1,\"email\":\"user@example.com\",\"nickname\":\"사용자\"}}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "로그인 실패 (잘못된 이메일 또는 비밀번호)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "실패 응답",
                    value = "{\"success\":false,\"message\":\"이메일 또는 비밀번호가 올바르지 않습니다.\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Parameter(description = "로그인 요청 정보", required = true)
        @RequestBody LoginRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("이메일을 입력해주세요."));
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("비밀번호를 입력해주세요."));
            }
            
            User user = userService.loginUser(request);
            
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("이메일 또는 비밀번호가 올바르지 않습니다."));
            }
            
            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            
            User responseUser = new User();
            responseUser.setId(user.getId());
            responseUser.setEmail(user.getEmail());
            responseUser.setNickname(user.getNickname());
            responseUser.setCreatedAt(user.getCreatedAt());
            
            AuthResponse authResponse = new AuthResponse(token, responseUser);
            
            return ResponseEntity.ok(
                ApiResponse.success("로그인이 완료되었습니다.", authResponse)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("로그인 처리 중 오류가 발생했습니다."));
        }
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃",
        description = "현재 로그인된 사용자를 로그아웃합니다. JWT 토큰이 필요합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = "{\"success\":true,\"message\":\"로그아웃이 완료되었습니다.\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            // 현재는 단순한 로그아웃 응답만 반환
            // 실제 세션 기반 인증을 사용한다면 세션 무효화 처리
            // JWT를 사용한다면 토큰 블랙리스트 처리 등을 할 수 있음
            
            return ResponseEntity.ok(
                ApiResponse.success("로그아웃이 완료되었습니다.", null)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("로그아웃 처리 중 오류가 발생했습니다."));
        }
    }
}
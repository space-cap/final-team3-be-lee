package com.ezlevup.jober.controller;

import com.ezlevup.jober.dto.ApiResponse;
import com.ezlevup.jober.dto.AuthResponse;
import com.ezlevup.jober.dto.LoginRequest;
import com.ezlevup.jober.dto.SignupRequest;
import com.ezlevup.jober.entity.User;
import com.ezlevup.jober.service.UserService;
import com.ezlevup.jober.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@RequestBody SignupRequest request) {
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
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
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
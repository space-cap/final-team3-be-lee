package com.ezlevup.jober.controller;

import com.ezlevup.jober.dto.ApiResponse;
import com.ezlevup.jober.dto.SignupRequest;
import com.ezlevup.jober.entity.User;
import com.ezlevup.jober.service.UserService;
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
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignupRequest request) {
        try {
            Map<String, String> validationErrors = userService.validateSignupRequest(request);
            
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("입력값에 오류가 있습니다.", validationErrors));
            }
            
            User user = userService.createUser(request);
            
            User responseUser = new User();
            responseUser.setId(user.getId());
            responseUser.setEmail(user.getEmail());
            responseUser.setNickname(user.getNickname());
            responseUser.setCreatedAt(user.getCreatedAt());
            
            return ResponseEntity.ok(
                ApiResponse.success("회원가입이 완료되었습니다.", responseUser)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("회원가입 처리 중 오류가 발생했습니다."));
        }
    }
}
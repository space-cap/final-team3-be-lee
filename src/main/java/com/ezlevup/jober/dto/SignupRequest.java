package com.ezlevup.jober.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {
    
    @Schema(description = "이메일 주소", example = "user@example.com", required = true)
    private String email;
    
    @Schema(description = "비밀번호 (8자 이상)", example = "password123", required = true)
    private String password;
    
    @Schema(description = "닉네임", example = "사용자", required = true)
    private String nickname;
}
package com.ezlevup.jober.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "템플릿 생성 요청")
public class TemplateGenerateRequest {
    
    @Schema(description = "사용자 프롬프트", example = "학부모님께 과제 안내 템플릿 생성")
    private String prompt;
    
    @Schema(description = "대상 고객", example = "학부모")
    private String targetCustomer;
    
    @Schema(description = "템플릿 목적", example = "과제 안내")
    private String purpose;
    
    @Schema(description = "비즈니스 타입", example = "교육")
    private String businessType;
    
    @Schema(description = "메시지 목적", example = "공지사항")
    private String messagePurpose;
}
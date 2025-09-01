package com.ezlevup.jober.controller;

import com.ezlevup.jober.dto.ApiResponse;
import com.ezlevup.jober.dto.TemplateGenerateRequest;
import com.ezlevup.jober.entity.Template;
import com.ezlevup.jober.service.TemplateService;
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

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "템플릿", description = "카카오톡 알림톡 템플릿 생성 및 관리 API")
public class TemplateController {
    
    private final TemplateService templateService;
    
    @PostMapping("/generate")
    @Operation(
        summary = "AI 템플릿 생성",
        description = "사용자 프롬프트를 기반으로 AI가 카카오톡 알림톡 템플릿을 자동 생성합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "템플릿 생성 요청 정보",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TemplateGenerateRequest.class),
                examples = @ExampleObject(
                    name = "템플릿 생성 예제",
                    value = "{\"prompt\":\"학부모님께 과제 안내 템플릿 생성\",\"targetCustomer\":\"학부모\",\"purpose\":\"과제 안내\"}"
                )
            )
        )
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "템플릿 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = "{\"success\":true,\"message\":\"템플릿이 성공적으로 생성되었습니다.\",\"data\":{\"templateId\":\"TPL_001\",\"title\":\"[과제 안내]\",\"content\":\"안녕하세요, #{고객명}학부모님...\",\"variables\":[{\"key\":\"#{고객명}\",\"sampleValue\":\"홍길동\"}]}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "실패 응답",
                    value = "{\"success\":false,\"message\":\"프롬프트를 입력해주세요.\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Template>> generateTemplate(
        @Parameter(description = "템플릿 생성 요청 정보", required = true)
        @RequestBody TemplateGenerateRequest request,
        @Parameter(description = "사용자 ID", hidden = true)
        @RequestAttribute(value = "userId", required = false) Long userId) {
        
        try {
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("프롬프트를 입력해주세요."));
            }
            
            Template template = templateService.generateTemplate(request, userId != null ? userId : 0L);
            
            return ResponseEntity.ok(
                ApiResponse.success("템플릿이 성공적으로 생성되었습니다.", template)
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("템플릿 생성 중 오류가 발생했습니다."));
        }
    }
    
    @GetMapping("/my")
    @Operation(
        summary = "내 템플릿 목록 조회",
        description = "현재 로그인된 사용자가 생성한 템플릿 목록을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = "{\"success\":true,\"message\":\"템플릿 목록을 조회했습니다.\",\"data\":[{\"templateId\":\"TPL_001\",\"title\":\"[과제 안내]\",\"createdAt\":\"2024-01-01T10:00:00\"}]}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<Template>>> getMyTemplates(
        @Parameter(description = "사용자 ID", hidden = true)
        @RequestAttribute(value = "userId", required = false) Long userId) {
        
        try {
            List<Template> templates = templateService.getUserTemplates(userId);
            
            return ResponseEntity.ok(
                ApiResponse.success("템플릿 목록을 조회했습니다.", templates)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("템플릿 목록 조회 중 오류가 발생했습니다."));
        }
    }
}
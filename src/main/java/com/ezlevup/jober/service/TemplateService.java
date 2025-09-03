package com.ezlevup.jober.service;

import com.ezlevup.jober.dto.TemplateGenerateRequest;
import com.ezlevup.jober.entity.Template;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String AI_API_URL = "http://localhost:8000/generate-template";
    
    public static class AITemplateRequest {
        @JsonProperty("user_input")
        private String userInput;
        
        @JsonProperty("business_type")
        private String businessType;
        
        @JsonProperty("message_purpose")
        private String messagePurpose;
        
        public AITemplateRequest(String userInput, String businessType, String messagePurpose) {
            this.userInput = userInput;
            this.businessType = businessType;
            this.messagePurpose = messagePurpose;
        }
        
        public String getUserInput() { return userInput; }
        public String getBusinessType() { return businessType; }
        public String getMessagePurpose() { return messagePurpose; }
    }
    
    
    public Template generateTemplate(TemplateGenerateRequest request, Long userId) {
        try {
            logger.info("Starting template generation for userId: {}, prompt: {}", userId, request.getPrompt());
            
            AITemplateRequest aiRequest = new AITemplateRequest(
                request.getPrompt(),
                request.getBusinessType(),
                request.getMessagePurpose()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AITemplateRequest> requestEntity = new HttpEntity<>(aiRequest, headers);
            
            logger.info("Calling AI API at: {}", AI_API_URL);
            String aiResponseJson = restTemplate.postForObject(
                AI_API_URL, 
                requestEntity, 
                String.class
            );
            
            logger.info("AI API response received: {}", aiResponseJson != null ? "Success" : "Null");
            
            Template template = new Template();
            
            if (aiResponseJson != null && !aiResponseJson.isEmpty()) {
                logger.info("Setting AI response to template");
                template.setAiRes(aiResponseJson);
                logger.info("Successfully processed AI response");
            } else {
                logger.warn("AI response is null or empty, using fallback template");
                generateFallbackTemplate(template, request);
            }
            
            return template;
            
        } catch (Exception e) {
            logger.error("Error calling AI API: {}", e.getMessage());
            Template template = new Template();
            generateFallbackTemplate(template, request);
            return template;
        }
    }
    
    private void generateFallbackTemplate(Template template, TemplateGenerateRequest request) {
        logger.info("Generating fallback template for prompt: {}", request.getPrompt());
        
        String fallbackJson;
        if (request.getPrompt().contains("과제")) {
            fallbackJson = "{\n" +
                "  \"title\": \"[과제 안내]\",\n" +
                "  \"content\": \"안녕하세요, #{고객명}학부모님.\\n#{과목명} 과제 관련 안내드립니다.\\n\\n📝 과제명: 과제 제출 안내\\n\\n문의 사항은 연락처 #{연락처}로 연락 주세요.\",\n" +
                "  \"buttons\": [{\"type\": \"WL\", \"text\": \"과제 확인하기\", \"link\": \"https://school.jober.io/homework/123\"}],\n" +
                "  \"variables\": [{\"key\": \"#{고객명}\", \"sampleValue\": \"홍길동\"}, {\"key\": \"#{과목명}\", \"sampleValue\": \"가을학기 오리엔테이션\"}, {\"key\": \"#{연락처}\", \"sampleValue\": \"010-1234-5678\"}]\n" +
                "}";
        } else if (request.getPrompt().contains("수업")) {
            fallbackJson = "{\n" +
                "  \"title\": \"[수업 일정 안내]\",\n" +
                "  \"content\": \"안녕하세요, #{고객명}님.\\n#{수업명} 일정이 변경되었습니다.\\n\\n📅 변경된 일정: #{새_일정}\\n\\n궁금한 사항은 #{연락처}로 문의해주세요.\",\n" +
                "  \"buttons\": [{\"type\": \"WL\", \"text\": \"일정 확인하기\", \"link\": \"https://school.jober.io/schedule/456\"}],\n" +
                "  \"variables\": [{\"key\": \"#{고객명}\", \"sampleValue\": \"김철수\"}, {\"key\": \"#{수업명}\", \"sampleValue\": \"프로그래밍 기초\"}, {\"key\": \"#{새_일정}\", \"sampleValue\": \"매주 화요일 오후 2시\"}, {\"key\": \"#{연락처}\", \"sampleValue\": \"010-9876-5432\"}]\n" +
                "}";
        } else {
            fallbackJson = "{\n" +
                "  \"title\": \"[안내사항]\",\n" +
                "  \"content\": \"안녕하세요, #{고객명}님.\\n#{내용}에 대해 안내드립니다.\\n\\n자세한 사항은 #{연락처}로 문의해주세요.\",\n" +
                "  \"buttons\": [{\"type\": \"WL\", \"text\": \"자세히 보기\", \"link\": \"https://jober.io/info\"}],\n" +
                "  \"variables\": [{\"key\": \"#{고객명}\", \"sampleValue\": \"고객님\"}, {\"key\": \"#{내용}\", \"sampleValue\": \"중요한 공지사항\"}, {\"key\": \"#{연락처}\", \"sampleValue\": \"010-0000-0000\"}]\n" +
                "}";
        }
        
        template.setAiRes(fallbackJson);
    }
    
    public List<Template> getUserTemplates(Long userId) {
        // 실제로는 데이터베이스에서 조회
        // 현재는 빈 리스트 반환
        return Arrays.asList();
    }
}
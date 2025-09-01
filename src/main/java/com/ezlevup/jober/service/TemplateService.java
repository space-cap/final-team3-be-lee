package com.ezlevup.jober.service;

import com.ezlevup.jober.dto.TemplateGenerateRequest;
import com.ezlevup.jober.entity.Template;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TemplateService {
    
    private final AtomicLong templateIdGenerator = new AtomicLong(1);
    
    public Template generateTemplate(TemplateGenerateRequest request, Long userId) {
        // AI 생성 시뮬레이션 (실제로는 OpenAI API 등을 호출)
        String templateId = "TPL_" + String.format("%03d", templateIdGenerator.getAndIncrement());
        
        // 프롬프트 기반 템플릿 생성 로직 시뮬레이션
        Template template = new Template();
        template.setTemplateId(templateId);
        template.setUserId(userId);
        template.setCreatedAt(LocalDateTime.now());
        
        // 프롬프트에 따른 다양한 템플릿 생성
        if (request.getPrompt().contains("과제")) {
            template.setTitle("[과제 안내]");
            template.setContent("안녕하세요, #{고객명}학부모님.\n#{과목명} 과제 관련 안내드립니다.\n\n📝 과제명: 과제 제출 안내\n\n문의 사항은 연락처 #{연락처}로 연락 주세요.");
            
            template.setButtons(Arrays.asList(
                new Template.TemplateButton("WL", "과제 확인하기", "https://school.jober.io/homework/123")
            ));
            
            template.setVariables(Arrays.asList(
                new Template.TemplateVariable("#{고객명}", "홍길동"),
                new Template.TemplateVariable("#{과목명}", "가을학기 오리엔테이션"),
                new Template.TemplateVariable("#{연락처}", "010-1234-5678")
            ));
        } else if (request.getPrompt().contains("수업")) {
            template.setTitle("[수업 일정 안내]");
            template.setContent("안녕하세요, #{고객명}님.\n#{수업명} 일정이 변경되었습니다.\n\n📅 변경된 일정: #{새_일정}\n\n궁금한 사항은 #{연락처}로 문의해주세요.");
            
            template.setButtons(Arrays.asList(
                new Template.TemplateButton("WL", "일정 확인하기", "https://school.jober.io/schedule/456")
            ));
            
            template.setVariables(Arrays.asList(
                new Template.TemplateVariable("#{고객명}", "김철수"),
                new Template.TemplateVariable("#{수업명}", "프로그래밍 기초"),
                new Template.TemplateVariable("#{새_일정}", "매주 화요일 오후 2시"),
                new Template.TemplateVariable("#{연락처}", "010-9876-5432")
            ));
        } else {
            // 기본 템플릿
            template.setTitle("[안내사항]");
            template.setContent("안녕하세요, #{고객명}님.\n#{내용}에 대해 안내드립니다.\n\n자세한 사항은 #{연락처}로 문의해주세요.");
            
            template.setButtons(Arrays.asList(
                new Template.TemplateButton("WL", "자세히 보기", "https://jober.io/info")
            ));
            
            template.setVariables(Arrays.asList(
                new Template.TemplateVariable("#{고객명}", "고객님"),
                new Template.TemplateVariable("#{내용}", "중요한 공지사항"),
                new Template.TemplateVariable("#{연락처}", "010-0000-0000")
            ));
        }
        
        return template;
    }
    
    public List<Template> getUserTemplates(Long userId) {
        // 실제로는 데이터베이스에서 조회
        // 현재는 빈 리스트 반환
        return Arrays.asList();
    }
}
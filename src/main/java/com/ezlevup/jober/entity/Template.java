package com.ezlevup.jober.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {
    private String templateId;
    private String title;
    private String content;
    private List<TemplateButton> buttons;
    private List<TemplateVariable> variables;
    private LocalDateTime createdAt;
    private Long userId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateButton {
        private String type;
        private String text;
        private String link;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateVariable {
        private String key;
        private String sampleValue;
    }
}
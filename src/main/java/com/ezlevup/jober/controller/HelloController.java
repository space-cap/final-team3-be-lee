package com.ezlevup.jober.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {

    /**
     * React 애플리케이션으로부터의 API 요청을 처리하는 엔드포인트입니다.
     * @return JSON 형태의 메시지를 담은 Map 객체
     */
    @GetMapping("/api/hello")
    // @CrossOrigin 어노테이션은 다른 도메인(여기서는 React 개발 서버)에서의 요청을 허용하기 위해 필요합니다.
    // 실제 프로덕션 환경에서는 특정 도메인만 허용하도록 설정하는 것이 좋습니다. (예: @CrossOrigin(origins = "http://localhost:3000" ))
    @CrossOrigin(origins = "*")
    public Map<String, String> hello() {
        // 클라이언트에게 보낼 데이터를 Map 형태로 생성합니다.
        // Spring Boot는 자동으로 이 Map을 JSON 형태로 변환하여 응답합니다.
        // 예: {"message": "안녕하세요! Spring Boot로부터의 메시지입니다."}
        return Collections.singletonMap("message", "안녕하세요! Spring Boot로부터의 메시지입니다.");
    }
}


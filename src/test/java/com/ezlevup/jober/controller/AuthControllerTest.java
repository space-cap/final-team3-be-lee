package com.ezlevup.jober.controller;

import com.ezlevup.jober.dto.SignupRequest;
import com.ezlevup.jober.entity.User;
import com.ezlevup.jober.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 - 성공")
    void signup_success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("success@example.com");
        request.setPassword("password123");
        request.setNickname("성공테스터");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/signup", entity, Map.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat((Boolean) response.getBody().get("success")).isTrue();
        assertThat((String) response.getBody().get("message")).isEqualTo("회원가입이 완료되었습니다.");
        
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertThat((String) data.get("email")).isEqualTo("success@example.com");
        assertThat((String) data.get("nickname")).isEqualTo("성공테스터");
        assertThat(data.get("password")).isNull();
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시도 - 실패")
    void signup_duplicateEmail_fail() throws Exception {
        // 기존 사용자 생성
        User existingUser = new User();
        existingUser.setEmail("duplicate@example.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setNickname("기존사용자");
        userRepository.save(existingUser);

        SignupRequest request = new SignupRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");
        request.setNickname("새사용자");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/signup", entity, Map.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat((Boolean) response.getBody().get("success")).isFalse();
        
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors.get("email")).isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("빈 이메일로 회원가입 시도 - 실패")
    void signup_emptyEmail_fail() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setPassword("password123");
        request.setNickname("빈이메일테스터");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/signup", entity, Map.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat((Boolean) response.getBody().get("success")).isFalse();
        
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors.get("email")).isEqualTo("이메일은 필수입니다.");
    }

    @Test
    @DisplayName("짧은 비밀번호로 회원가입 시도 - 실패")
    void signup_shortPassword_fail() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("shortpw@example.com");
        request.setPassword("123");
        request.setNickname("짧은비번테스터");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/signup", entity, Map.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat((Boolean) response.getBody().get("success")).isFalse();
        
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors.get("password")).isEqualTo("비밀번호는 8자 이상이어야 합니다.");
    }
}
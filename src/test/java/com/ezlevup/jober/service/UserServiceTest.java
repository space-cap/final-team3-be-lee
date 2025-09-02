package com.ezlevup.jober.service;

import com.ezlevup.jober.dto.SignupRequest;
import com.ezlevup.jober.entity.User;
import com.ezlevup.jober.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@org.springframework.test.context.TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 검증 - 성공")
    void validateSignupRequest_success() {
        SignupRequest request = new SignupRequest();
        request.setEmail("valid@example.com");
        request.setPassword("password123");
        request.setNickname("유효테스터");

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("빈 이메일 검증 - 실패")
    void validateSignupRequest_emptyEmail_fail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setPassword("password123");
        request.setNickname("테스터");

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.get("email")).isEqualTo("이메일은 필수입니다.");
    }

    @Test
    @DisplayName("잘못된 이메일 형식 검증 - 실패")
    void validateSignupRequest_invalidEmail_fail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setNickname("테스터");

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.get("email")).isEqualTo("올바른 이메일 형식이 아닙니다.");
    }

    @Test
    @DisplayName("중복 이메일 검증 - 실패")
    void validateSignupRequest_duplicateEmail_fail() {
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

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.get("email")).isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("짧은 비밀번호 검증 - 실패")
    void validateSignupRequest_shortPassword_fail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("shortpw@example.com");
        request.setPassword("123");
        request.setNickname("테스터");

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.get("password")).isEqualTo("비밀번호는 8자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("빈 닉네임 검증 - 실패")
    void validateSignupRequest_emptyNickname_fail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("emptynick@example.com");
        request.setPassword("password123");
        request.setNickname("");

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.get("nickname")).isEqualTo("닉네임은 필수입니다.");
    }

    @Test
    @DisplayName("중복 닉네임 검증 - 실패")
    void validateSignupRequest_duplicateNickname_fail() {
        // 기존 사용자 생성
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setNickname("테스터");
        userRepository.save(existingUser);

        SignupRequest request = new SignupRequest();
        request.setEmail("newnick@example.com");
        request.setPassword("password123");
        request.setNickname("테스터");

        Map<String, String> errors = userService.validateSignupRequest(request);

        assertThat(errors).hasSize(1);
        assertThat(errors.get("nickname")).isEqualTo("이미 사용 중인 닉네임입니다.");
    }

    @Test
    @DisplayName("사용자 생성 - 성공")
    void createUser_success() {
        SignupRequest request = new SignupRequest();
        request.setEmail("create@example.com");
        request.setPassword("password123");
        request.setNickname("생성테스터");

        User createdUser = userService.createUser(request);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("create@example.com");
        assertThat(createdUser.getNickname()).isEqualTo("생성테스터");
        assertThat(createdUser.getPassword()).isNotEqualTo("password123"); // 암호화됨
        assertThat(createdUser.getCreatedAt()).isNotNull();
        assertThat(createdUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("비밀번호 암호화 확인")
    void createUser_passwordEncryption() {
        SignupRequest request = new SignupRequest();
        request.setEmail("encrypt@example.com");
        request.setPassword("password123");
        request.setNickname("암호화테스터");

        User createdUser = userService.createUser(request);

        // BCrypt 패턴 확인 ($2a$ 또는 $2b$로 시작)
        assertThat(createdUser.getPassword()).startsWith("$2");
        assertThat(createdUser.getPassword().length()).isGreaterThan(50);
    }
}
package com.ezlevup.jober.service;

import com.ezlevup.jober.dto.LoginRequest;
import com.ezlevup.jober.dto.SignupRequest;
import com.ezlevup.jober.entity.User;
import com.ezlevup.jober.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Map<String, String> validateSignupRequest(SignupRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.put("email", "이메일은 필수입니다.");
        } else if (!isValidEmail(request.getEmail())) {
            errors.put("email", "올바른 이메일 형식이 아닙니다.");
        } else if (userRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "이미 사용 중인 이메일입니다.");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            errors.put("password", "비밀번호는 필수입니다.");
        } else if (request.getPassword().length() < 8) {
            errors.put("password", "비밀번호는 8자 이상이어야 합니다.");
        }
        
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            errors.put("nickname", "닉네임은 필수입니다.");
        } else if (userRepository.existsByNickname(request.getNickname())) {
            errors.put("nickname", "이미 사용 중인 닉네임입니다.");
        }
        
        return errors;
    }
    
    public User createUser(SignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setNickname(request.getNickname());
        
        return userRepository.save(user);
    }
    
    public User loginUser(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return user;
        }
        
        return null;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
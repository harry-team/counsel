package com.harry.counsel.java.domain.user.controller;

import com.harry.counsel.java.domain.user.dto.UserResponseDto;
import com.harry.counsel.java.domain.user.entity.User;
import com.harry.counsel.java.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> authSuccess(@RequestParam String token) {
        log.info("authSuccess start!! : {}", token);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        UserResponseDto build = UserResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .id(user.getId())
                .build();

        return ResponseEntity.ok(build);
    }
}

package com.harry.counsel.java.domain.user.service;

import com.harry.counsel.java.domain.user.dto.OAuth2UserDto;
import com.harry.counsel.java.domain.user.entity.User;
import com.harry.counsel.java.domain.user.entity.UserRole;
import com.harry.counsel.java.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[OAuth2] Loading user - Client: {}, AccessToken: {}",
                userRequest.getClientRegistration().getRegistrationId(),
                userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserDto userInfo = extractUserInfo(oAuth2User, userRequest);

        User user = getOrCreateUser(userInfo);

        return new DefaultOAuth2User(
                Collections.singletonList(null),
                oAuth2User.getAttributes(),
                "email"
        );
    }

    private OAuth2UserDto extractUserInfo(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String socialId = (String) response.get("id");
        String email = (String) response.get("email");
        String name = (String) response.get("name");

        log.info("[OAuth2] Parsed user info → Provider: {}, ID: {}, Email: {}, Name: {}",
                provider, socialId, email, name);

        return new OAuth2UserDto(provider, socialId, email, name);
    }

    private User getOrCreateUser(OAuth2UserDto userInfo) {
        return userRepository.findBySocialIdAndProvider(userInfo.getSocialId(), userInfo.getProvider())
                .orElseGet(() -> {
                    log.info("[OAuth2] User not found in DB, creating new user...");

                    User newUser = User.builder()
                            .socialId(userInfo.getSocialId())
                            .provider(userInfo.getProvider())
                            .email(userInfo.getEmail())
                            .name(userInfo.getName())
                            .role(UserRole.valueOf(UserRole.ROLE_USER.name()))
                            .build();
                    log.info("[OAuth2] New user : {}", newUser);

                    User savedUser = userRepository.save(newUser);
                    log.info("[OAuth2] New user saved with ID: {}", savedUser.getId());
                    return savedUser;
                });
    }
}

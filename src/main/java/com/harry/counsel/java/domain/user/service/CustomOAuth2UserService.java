package com.harry.counsel.java.domain.user.service;

import com.harry.counsel.java.domain.user.dto.OAuth2UserDto;
import com.harry.counsel.java.domain.user.entity.User;
import com.harry.counsel.java.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

        getOrCreateUser(userInfo);

        // 속성 맵 복사 및 email 강제 설정
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("email", userInfo.email()); // 추출된 email로 덮어쓰기

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new DefaultOAuth2User(
                authorities,
                attributes,
                "email"
        );
    }

    private OAuth2UserDto extractUserInfo(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("[OAuth2] Provider: {}, Attributes: {}", provider, attributes);

        String socialId;
        String email;
        String name;

        if ("google".equals(provider)) {
            socialId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) {
                log.error("[OAuth2] Naver response is null");
                throw new OAuth2AuthenticationException("Naver response is null");
            }
            socialId = (String) response.get("id");
            email = (String) response.get("email");
            name = (String) response.get("name");
        } else if ("apple".equals(provider)) {
            socialId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = attributes.get("name") != null ? attributes.get("name").toString() : "Unknown";
            log.info("[OAuth2] Apple email: {}", email);
        } else {
            log.error("[OAuth2] Unsupported provider: {}", provider);
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        log.info("[OAuth2] Extracted - socialId: {}, email: {}, name: {}", socialId, email, name);

        // email이 null인 경우 임시 이메일 생성
        if (email == null) {
            log.warn("[OAuth2] Email is null for provider: {}. Generating temporary email.", provider);
            email = socialId + "@" + provider + ".unknown";
        }

        // name이 null인 경우 기본값 설정
        if (name == null) {
            name = "Unknown";
        }

        return new OAuth2UserDto(provider, socialId, email, name);
    }

    private void getOrCreateUser(OAuth2UserDto userInfo) {
        userRepository.findBySocialIdAndProvider(userInfo.socialId(), userInfo.provider())
                .orElseGet(() -> {
                    log.info("[OAuth2] User not found in DB, creating new user...");

                    User newUser = User.builder()
                            .socialId(userInfo.socialId())
                            .provider(userInfo.provider())
                            .email(userInfo.email())
                            .name(userInfo.name())
                            .build();
                    log.info("[OAuth2] New user : {}", newUser);

                    User savedUser = userRepository.save(newUser);
                    log.info("[OAuth2] New user saved with ID: {}", savedUser.getId());
                    return savedUser;
                });
    }
}

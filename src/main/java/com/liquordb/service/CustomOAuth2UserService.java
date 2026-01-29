package com.liquordb.service;

import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * 소셜 로그인 기능 제공을 위한 OAuth2 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(request);

        // 어떤 OAuth provider인지 (google, naver 등)
        String registrationId = request.getClientRegistration().getRegistrationId();

        // 인증 정보의 고유 식별자 키 (ex: sub, id, email 등)
        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oauth2User.getAttributes();

        // 공통 정보 추출 (이메일, 이름)
        String email;
        String name;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
            attributes = response; // naver는 response 내부를 attributes로 사용해야 함
        } else {
            email = null;
            name = null;
            throw new IllegalArgumentException("Unsupported OAuth provider: " + registrationId);
        }

        // 이메일로 유저 조회 또는 새로 생성
        User user = userRepository.findByEmailAndStatusNot(email, UserStatus.BANNED)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .username(name != null ? name : "noname")
                        .role(User.Role.USER)
                        .build()
                ));

        // OAuth 인증된 사용자 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                userNameAttributeName // → provider의 고유 사용자 식별 key
        );
    }
}

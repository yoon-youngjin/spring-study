package dev.yoon.auth.infra;

import dev.yoon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class NaverOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    /**
     * OAUTH의 과정이 전부 일어난 후 함수에 들어가는 순간
     * 사용자의 정보를 GET
     * 나의 서비스에서 사용자의 정보를 어떻게 사용할 지를 작성
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String nameAttributeKey = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributeMap = oAuth2User.getAttributes();

        Map<String, Object> responseMap = (Map<String, Object>) attributeMap.get("response");

        log.info(responseMap.toString());

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                responseMap,
                "email"
        );
        return defaultOAuth2User;
    }
}

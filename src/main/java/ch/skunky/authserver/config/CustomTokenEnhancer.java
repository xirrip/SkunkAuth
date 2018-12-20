package ch.skunky.authserver.config;

import ch.skunky.authserver.model.User;
import ch.skunky.authserver.repository.UserRepository;
import ch.skunky.authserver.service.SkunkUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();

        SkunkUserPrincipal principal = (SkunkUserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        additionalInfo.put("username", user.getUsername());
        additionalInfo.put("email", user.getEmail());
        additionalInfo.put("grants", user.getRoles());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}

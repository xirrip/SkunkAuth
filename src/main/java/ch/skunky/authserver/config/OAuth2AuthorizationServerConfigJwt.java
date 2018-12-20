package ch.skunky.authserver.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Official Spring IO guide: (very long)
 * https://spring.io/guides/tutorials/spring-security-and-angular-js/
 */

/**
 * Testing the token service:
 * curl fooClientIdPassword:secret@localhost:8081/spring-security-oauth-server/oauth/token -d grant_type=password -d username=tom -d password=111
 * -> returns a token! (it is still not available under /tokens endpoint?!)
 * using it afterwards in curl:
 * curl localhost:8082/clients -H"Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ0b20iLCJzY29wZSI6WyJmb28iLCJyZWFkIiwid3JpdGUiXSwib3JnYW5pemF0aW9uIjoidG9tWkpabyIsImV4cCI6MTU0NDg4MTI3MiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiI0YjY2YWEyNy1lNDVjLTRmNjYtYjBkZC02M2MzMDU0MGI4MGQiLCJjbGllbnRfaWQiOiJmb29DbGllbnRJZFBhc3N3b3JkIn0.ySEY8DEx215yfmLOzxKjtYQxfSW5Ll856aVqroKFMos"
 * works!
 *
 */

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigJwt extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("sampleClientId")
                    .authorizedGrantTypes("implicit")
                    .scopes("read", "write", "foo", "bar")
                    .autoApprove(false)
                    .accessTokenValiditySeconds(3600)
                    .redirectUris("http://localhost:8083/")

                .and().withClient("fooClientIdPassword")
                    .secret(passwordEncoder().encode("secret"))
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                    .scopes("foo", "read", "write")
                    // .accessTokenValiditySeconds(3600)
                    .accessTokenValiditySeconds(60)
                    // 1 hour
                    .refreshTokenValiditySeconds(2592000)
                    // 30 days
                    .redirectUris("xxx","http://localhost:8089/","http://localhost:8080/login/oauth2/code/custom")

                .and().withClient("barClientIdPassword")
                    .secret(passwordEncoder().encode("secret"))
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                    .scopes("bar", "read", "write")
                    .accessTokenValiditySeconds(3600)
                    // 1 hour
                    .refreshTokenValiditySeconds(2592000) // 30 days

                .and().withClient("testImplicitClientId")
                    .authorizedGrantTypes("implicit")
                    .scopes("read", "write", "foo", "bar")
                    .autoApprove(true)
                    .redirectUris("xxx");

    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain).authenticationManager(authenticationManager);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        // final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"), "mypass".toCharArray());
        // converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

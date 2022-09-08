package com.example.authorization.server.config;

import com.example.authorization.server.jose.Jwks;
import com.example.authorization.server.repository.AuthorizationConsentRepository;
import com.example.authorization.server.repository.AuthorizationRepository;
import com.example.authorization.server.repository.ClientRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Joe Grandja
 * @author Daniel Garnier-Moiroux
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer<>();
        authorizationServerConfigurer
                .oidc(oidc ->
                        oidc.clientRegistrationEndpoint(Customizer.withDefaults()))
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .formLogin(loginConfigurer -> {
                    loginConfigurer.loginPage("/login");
                })
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .apply(authorizationServerConfigurer);
        return http.build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer("http://localhost:9000")
                .authorizationEndpoint("/my/authorize")
                .build();
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {
        // Will be used by the ConsentController
        return new JpaOAuth2AuthorizationConsentService(authorizationConsentRepository, registeredClientRepository);
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(ClientRepository clientRepository) {
        JpaRegisteredClientRepository registeredClientRepository = new JpaRegisteredClientRepository(clientRepository);
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("test")
                .clientSecret("{noop}123456")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8082/callback")
                .redirectUri("http://127.0.0.1:8082/callback2")
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/test")
                .scope(OidcScopes.OPENID)
                .scope("message.read")
                .scope("message.write")
                .clientIdIssuedAt(Instant.now())
                .scope("client.create")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        if (registeredClientRepository.findByClientId(registeredClient.getClientId()) == null) {
            registeredClientRepository.save(registeredClient);
        }
        return registeredClientRepository;
    }

    @Bean
    OAuth2AuthorizationService authorizationService(
            AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationService(authorizationRepository, registeredClientRepository);
    }

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


}

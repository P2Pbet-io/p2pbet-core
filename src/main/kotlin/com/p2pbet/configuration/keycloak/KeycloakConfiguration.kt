package com.p2pbet.configuration.keycloak


import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
@EnableConfigurationProperties(KeycloakProperties::class)
class KeycloakConfiguration(
    val keycloakProperties: KeycloakProperties
) {
    @Bean
    fun keycloak(): Keycloak {
        return KeycloakBuilder.builder()
            .grantType(OAuth2Constants.PASSWORD)
            .serverUrl(keycloakProperties.serverUrl)
            .realm(keycloakProperties.realm)
            .clientId(keycloakProperties.client)
            .clientSecret(keycloakProperties.secret)
            .username(keycloakProperties.username)
            .password(keycloakProperties.password)
            .build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(keycloakProperties.issuerUrl) as NimbusJwtDecoder

        val withIssuer = JwtValidators.createDefaultWithIssuer(keycloakProperties.issuerUrl)
        val withAudience = DelegatingOAuth2TokenValidator(withIssuer)

        jwtDecoder.setJwtValidator(withAudience)

        return jwtDecoder
    }
}

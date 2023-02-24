package com.p2pbet.configuration.keycloak

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType

@Configuration
class ClientRegistrationConfiguration(
    private val keycloakProperties: KeycloakProperties
) {
    @Bean
    fun ClientRegistrationRepository(): ClientRegistrationRepository =
        InMemoryClientRegistrationRepository(
            ClientRegistration.withRegistrationId("keycloak")
                .clientId(keycloakProperties.client)
                .clientSecret(keycloakProperties.secret)
                .tokenUri(keycloakProperties.issuerUrl)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .build()
        )
}
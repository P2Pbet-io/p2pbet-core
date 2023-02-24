package com.p2pbet.configuration.keycloak.client

import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.AccessTokenResponse
import org.springframework.stereotype.Component

@Component
class KeycloakAuthorizationClient(
    val keycloak: Keycloak
) {
    fun authorize(): AccessTokenResponse = keycloak.tokenManager().accessToken
}
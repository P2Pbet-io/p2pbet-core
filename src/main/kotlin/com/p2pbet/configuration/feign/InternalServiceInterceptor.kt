package com.p2pbet.configuration.feign

import com.p2pbet.configuration.keycloak.client.KeycloakAuthorizationClient
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component

@Component
class InternalServiceInterceptor(
    private val keycloakAuthorizationClient: KeycloakAuthorizationClient
) : RequestInterceptor {

    companion object {
        const val JWT_PREFIX = "bearer"
    }

    override fun apply(template: RequestTemplate) {
        val accessToken = keycloakAuthorizationClient.authorize().token
        template.header(AUTHORIZATION, "$JWT_PREFIX $accessToken")
    }
}

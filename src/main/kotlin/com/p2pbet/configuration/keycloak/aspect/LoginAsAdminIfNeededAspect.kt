package com.p2pbet.configuration.keycloak.aspect


import com.p2pbet.configuration.keycloak.client.KeycloakAuthorizationClient
import mu.KLogger
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(2)
class LoginAsAdminIfNeededAspect(
    private val keycloakAuthorizationClient: KeycloakAuthorizationClient,
    private val jwtDecoder: JwtDecoder
) {
    companion object {
        private val logger: KLogger = KotlinLogging.logger { }
    }

    @Before("@annotation(LoginAsAdminIfNeeded)")
    fun loginAsAdmin(joinPoint: JoinPoint) {
        if (isUserAuthenticated()) {
            logger.trace { "User is already logged" }
            return
        }

        authenticateAsSystemUser()

        logger.trace { "Logged in as SYSTEM user" }
    }

    private fun isUserAuthenticated(): Boolean =
        SecurityContextHolder.getContext().authentication
            ?.let { it !is AnonymousAuthenticationToken }
            ?: false


    private fun authenticateAsSystemUser() {
        val systemJwtToken = keycloakAuthorizationClient.authorize()
        val jwt = jwtDecoder.decode(systemJwtToken.token)

        val authentication = JwtAuthenticationToken(jwt, listOf(SimpleGrantedAuthority("ROLE_SYSTEM")))
        authentication.isAuthenticated = true

        SecurityContextHolder.getContext().authentication = authentication
    }
}

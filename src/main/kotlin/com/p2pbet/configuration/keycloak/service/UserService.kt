package com.p2pbet.configuration.keycloak.service

import com.nimbusds.jose.shaded.json.JSONArray
import com.p2pbet.configuration.keycloak.model.ApplicationUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class UserService {

    companion object {
        const val ID_CLAIM_NAME = "sub"

        const val REALM_ROLES_CLAIM_NAME = "realm_access"
        const val ROLES_CLAIM_NAME = "roles"
        const val SYSTEM_USERNAME: String = "system"
    }

    fun getUser(): ApplicationUser {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("No security context")

        if (authentication.principal is Jwt) {
            val principal = authentication.principal as Jwt
            return createUser(principal)
        }

        throw RuntimeException("Non jwt auth")
    }

    private fun createUser(principal: Jwt): ApplicationUser = ApplicationUser(
        id = principal.getClaimAsString(ID_CLAIM_NAME),
        token = principal.tokenValue,
        accessRoles = createRoles(principal),
    )

    private fun createRoles(principal: Jwt): Set<String> =
        (principal.getClaimAsMap(REALM_ROLES_CLAIM_NAME)[ROLES_CLAIM_NAME] as JSONArray).toSet() as Set<String>

    fun getCurrentUserAccessToken(): String = getUser().token

    fun getCurrentUserId(): String =
        when (val principal = SecurityContextHolder.getContext().authentication?.principal) {
            is Jwt -> principal.getClaimAsString(ID_CLAIM_NAME)
            is UserDetails -> principal.username
            is String -> principal
            else -> throw RuntimeException("No user id in security context")
        }
}
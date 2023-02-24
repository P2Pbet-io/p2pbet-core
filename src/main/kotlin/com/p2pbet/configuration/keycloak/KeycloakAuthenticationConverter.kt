package com.p2pbet.configuration.keycloak

import com.nimbusds.jose.shaded.json.JSONArray
import com.p2pbet.configuration.keycloak.service.UserService.Companion.REALM_ROLES_CLAIM_NAME
import com.p2pbet.configuration.keycloak.service.UserService.Companion.ROLES_CLAIM_NAME
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component


@Component
class KeycloakAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

    companion object {
        const val AUTHORITY_PREFIX = "ROLE_"
    }

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = extractAuthorities(jwt)
        return JwtAuthenticationToken(jwt, authorities)
    }

    fun extractAuthorities(jwt: Jwt) =
        (jwt.getClaimAsMap(REALM_ROLES_CLAIM_NAME)[ROLES_CLAIM_NAME] as JSONArray)
            .map {
                SimpleGrantedAuthority(AUTHORITY_PREFIX + it.toString())
            }
}

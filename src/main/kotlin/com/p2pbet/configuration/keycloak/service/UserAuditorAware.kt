package com.p2pbet.configuration.keycloak.service

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserAuditorAware(
    private val userService: UserService
) : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val userId = userService
            .runCatching { getCurrentUserId() }
            .getOrElse { "no auth" }
        return Optional.of(userId)
    }
}
package com.p2pbet.configuration.keycloak.aspect

import mu.KLogger
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(1)
class ClearSecurityContextAspect {

    companion object {
        private val logger: KLogger = KotlinLogging.logger { }
    }


    @Before("@annotation(ClearSecurityContext)")
    fun clearSecurityContext(joinPoint: JoinPoint) {
        SecurityContextHolder.clearContext()
        logger.trace { "Security context cleared" }
    }
}
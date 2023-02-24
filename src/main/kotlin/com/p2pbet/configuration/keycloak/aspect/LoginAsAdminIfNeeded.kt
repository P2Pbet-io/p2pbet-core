package com.p2pbet.configuration.keycloak.aspect

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LoginAsAdminIfNeeded

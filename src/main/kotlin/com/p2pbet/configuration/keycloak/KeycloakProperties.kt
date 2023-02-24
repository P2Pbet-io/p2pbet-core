package com.p2pbet.configuration.keycloak

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    @field: NotBlank
    val realm: String,
    @field: NotBlank
    val client: String,
    @field: NotBlank
    val secret: String,
    @field: NotBlank
    val serverUrl: String,
    @field: NotBlank
    val username: String,
    @field: NotBlank
    val password: String,
    @field: NotBlank
    val issuerUrl: String,
)
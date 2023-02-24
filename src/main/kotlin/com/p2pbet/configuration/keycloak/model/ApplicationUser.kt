package com.p2pbet.configuration.keycloak.model

data class ApplicationUser(
    val id: String,
    val token: String,
    val accessRoles: Set<String>
)

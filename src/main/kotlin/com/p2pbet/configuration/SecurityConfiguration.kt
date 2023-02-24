package com.p2pbet.configuration

import com.p2pbet.configuration.keycloak.KeycloakAuthenticationConverter
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration(
    private val keycloakAuthenticationConverter: KeycloakAuthenticationConverter
) : WebSecurityConfigurerAdapter() {

    public override fun configure(http: HttpSecurity) {
        //@formatter:off
        http
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/**").hasRole("SYSTEM")
            .antMatchers("/public/**").permitAll()
            .antMatchers("/management/*").permitAll()
            .antMatchers("/swagger-ui/*").permitAll()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(keycloakAuthenticationConverter)
            .and()
            .and()
            .oauth2Client()
            .and()
            .formLogin().disable()
            .httpBasic().disable()
    }
}

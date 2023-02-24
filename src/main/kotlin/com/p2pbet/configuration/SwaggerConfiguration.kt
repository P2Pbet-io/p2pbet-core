package com.p2pbet.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestHandlerSelectors.basePackage
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.Parameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableSwagger2
@Configuration
class SwaggerConfiguration {

    companion object {
        const val BASE_REST_CONTROLLER_PACKAGE = "com.p2pbet"
    }

    @Bean
    fun docket(): Docket = Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(basePackage(BASE_REST_CONTROLLER_PACKAGE))
        .build()
        .globalOperationParameters(listOf(createAuthorizationParameter()))

    private fun createAuthorizationParameter(): Parameter {
        val parameterBuilder = ParameterBuilder()

        return parameterBuilder
            .name(HttpHeaders.AUTHORIZATION)
            .modelRef(ModelRef("string"))
            .parameterType("header")
            .description("bearer {token}")
            .required(false)
            .build()
    }
}

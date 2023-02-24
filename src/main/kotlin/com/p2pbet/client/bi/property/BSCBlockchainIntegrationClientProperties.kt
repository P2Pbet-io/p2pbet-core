package com.p2pbet.client.bi.property

import com.p2pbet.configuration.feign.CustomFeignLogger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.net.URL
import javax.validation.constraints.NotNull

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "external-service.blockchain-integration.bsc")
data class BSCBlockchainIntegrationClientProperties(
    @field: NotNull
    val url: URL,
    @field: NotNull
    val requestLogLevel: CustomFeignLogger.RequestLogLevel,
    @field: NotNull
    val responseLogLevel: CustomFeignLogger.ResponseLogLevel,
)

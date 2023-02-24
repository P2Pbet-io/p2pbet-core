package com.p2pbet.client.bi.configuration

import com.p2pbet.client.bi.api.*
import com.p2pbet.client.bi.property.AvalancheBlockchainIntegrationClientProperties
import com.p2pbet.configuration.feign.FeignClientConfigurationHelper
import com.p2pbet.configuration.feign.InternalServiceInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AvalancheBlockchainIntegrationClientProperties::class)
class AvalancheBlockchainIntegrationConfiguration(
    private val helper: FeignClientConfigurationHelper,
    private val properties: AvalancheBlockchainIntegrationClientProperties,
    private val interceptor: InternalServiceInterceptor,
) {
    @Bean
    fun avalancheAuctionWriteApi(): AuctionWriteApi = helper.buildClient(
        api = AuctionWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun avalancheBinaryWriteApi(): BinaryWriteApi = helper.buildClient(
        api = BinaryWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun avalancheCustomWriteApi(): CustomWriteApi = helper.buildClient(
        api = CustomWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun avalancheExecutionApi(): ExecutionApi = helper.buildClient(
        api = ExecutionApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun avalancheJackpotWriteApi(): JackpotWriteApi = helper.buildClient(
        api = JackpotWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )
}

package com.p2pbet.client.bi.configuration

import com.p2pbet.client.bi.api.*
import com.p2pbet.client.bi.property.BSCBlockchainIntegrationClientProperties
import com.p2pbet.configuration.feign.FeignClientConfigurationHelper
import com.p2pbet.configuration.feign.InternalServiceInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BSCBlockchainIntegrationClientProperties::class)
class BSCBlockchainIntegrationConfiguration(
    private val helper: FeignClientConfigurationHelper,
    private val properties: BSCBlockchainIntegrationClientProperties,
    private val interceptor: InternalServiceInterceptor,
) {
    @Bean
    fun bscAuctionWriteApi(): AuctionWriteApi = helper.buildClient(
        api = AuctionWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun bscBinaryWriteApi(): BinaryWriteApi = helper.buildClient(
        api = BinaryWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun bscCustomWriteApi(): CustomWriteApi = helper.buildClient(
        api = CustomWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun bscExecutionApi(): ExecutionApi = helper.buildClient(
        api = ExecutionApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun bscJackpotWriteApi(): JackpotWriteApi = helper.buildClient(
        api = JackpotWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )
}

package com.p2pbet.client.bi.configuration

import com.p2pbet.client.bi.api.*
import com.p2pbet.client.bi.property.TronBlockchainIntegrationClientProperties
import com.p2pbet.configuration.feign.FeignClientConfigurationHelper
import com.p2pbet.configuration.feign.InternalServiceInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(TronBlockchainIntegrationClientProperties::class)
class TronBlockchainIntegrationConfiguration(
    private val helper: FeignClientConfigurationHelper,
    private val properties: TronBlockchainIntegrationClientProperties,
    private val interceptor: InternalServiceInterceptor,
) {
    @Bean
    fun tronAuctionWriteApi(): AuctionWriteApi = helper.buildClient(
        api = AuctionWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun tronBinaryWriteApi(): BinaryWriteApi = helper.buildClient(
        api = BinaryWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun tronCustomWriteApi(): CustomWriteApi = helper.buildClient(
        api = CustomWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun tronExecutionApi(): ExecutionApi = helper.buildClient(
        api = ExecutionApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

    @Bean
    fun tronJackpotWriteApi(): JackpotWriteApi = helper.buildClient(
        api = JackpotWriteApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )
}

package com.p2pbet.client.free.auction.configuration

import com.p2pbet.client.free.auction.api.FreeAuctionApi
import com.p2pbet.client.free.auction.property.FreeAuctionClientProperties
import com.p2pbet.configuration.feign.FeignClientConfigurationHelper
import com.p2pbet.configuration.feign.InternalServiceInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FreeAuctionClientProperties::class)
class FreeAuctionConfiguration(
    private val helper: FeignClientConfigurationHelper,
    private val properties: FreeAuctionClientProperties,
    private val interceptor: InternalServiceInterceptor,
) {
    @Bean
    fun freeAuctionApi(): FreeAuctionApi = helper.buildClient(
        api = FreeAuctionApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(interceptor)
    )

}

package com.p2pbet.client.dataproviders.finnhub.configuration

import com.p2pbet.client.dataproviders.finnhub.api.FinnHubApi
import com.p2pbet.client.dataproviders.finnhub.property.FinnHubClientProperties
import com.p2pbet.configuration.feign.FeignClientConfigurationHelper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FinnHubClientProperties::class)
class FinnHubFeignConfiguration(
    private val helper: FeignClientConfigurationHelper,
    private val properties: FinnHubClientProperties,
    private val funhubInterceptor: FinhubServiceInterceptor
) {
    @Bean
    fun finnHubApi(): FinnHubApi = helper.buildClient(
        api = FinnHubApi::class.java,
        url = properties.url.toString(),
        requestLogLevel = properties.requestLogLevel,
        responseLogLevel = properties.responseLogLevel,
        interceptors = listOf(funhubInterceptor)
    )
    @Bean
    fun finnHubApiToken(): String = properties.apiKey
}

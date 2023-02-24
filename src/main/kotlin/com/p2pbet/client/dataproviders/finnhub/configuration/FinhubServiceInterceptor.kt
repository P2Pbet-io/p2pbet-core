package com.p2pbet.client.dataproviders.finnhub.configuration

import com.p2pbet.client.dataproviders.finnhub.property.FinnHubClientProperties
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.stereotype.Component

@Component
class FinhubServiceInterceptor(
    private val finnHubClientProperties: FinnHubClientProperties
) : RequestInterceptor {

    companion object {
        const val FUNHUB_KEY_HEADER = "X-Finnhub-Token"
    }

    override fun apply(template: RequestTemplate) {
        template.header(FUNHUB_KEY_HEADER, finnHubClientProperties.apiKey)
    }
}

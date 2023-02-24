package com.p2pbet.configuration.feign


import feign.*
import feign.codec.Decoder
import feign.codec.Encoder
import feign.codec.ErrorDecoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component

@Component
@Import(FeignClientsConfiguration::class)
class FeignClientConfigurationHelper(
    private val defaultContract: Contract,
    messageConverters: ObjectFactory<HttpMessageConverters>
) {
    val springEncoder = SpringEncoder(messageConverters)
    val springDecoder = ResponseEntityDecoder(SpringDecoder(messageConverters))

    fun <T> buildClient(
        api: Class<T>,
        url: String,
        requestLogLevel: CustomFeignLogger.RequestLogLevel = CustomFeignLogger.RequestLogLevel.URL,
        responseLogLevel: CustomFeignLogger.ResponseLogLevel = CustomFeignLogger.ResponseLogLevel.STATUS,
        interceptors: List<RequestInterceptor> = listOf(),
        encoder: Encoder? = springEncoder,
        decoder: Decoder? = springDecoder,
        errorDecoder: ErrorDecoder = ErrorDecoder.Default(),
        retryer: Retryer? = Retryer.Default(),
        contract: Contract = defaultContract,
        metered: Boolean = false
    ): T =
        Feign.builder()
            .encoder(encoder)
            .decoder(decoder)
            .errorDecoder(errorDecoder)
            .requestInterceptors(interceptors)
            .retryer(retryer)
            .contract(contract)
            .logger(CustomFeignLogger(requestLogLevel, responseLogLevel))
            .logLevel(Logger.Level.BASIC)
            .target(api, url)
}
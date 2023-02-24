package com.p2pbet.configuration.feign


import feign.Logger
import feign.Request
import feign.Response
import feign.Util
import mu.KLogger
import mu.KotlinLogging

class CustomFeignLogger(
    private val requestLogLevel: RequestLogLevel,
    private val responseLogLevel: ResponseLogLevel
) : Logger() {
    private val logger: KLogger = KotlinLogging.logger { }

    enum class RequestLogLevel {
        NONE,
        URL,
        URL_BODY,
        URL_BODY_HEADERS
    }

    enum class ResponseLogLevel {
        NONE,
        STATUS,
        STATUS_BODY,
        STATUS_BODY_HEADERS
    }

    override fun logRequest(configKey: String, logLevel: Level, request: Request) {
        if (requestLogLevel == RequestLogLevel.NONE) return

        var requestLog = "REQUEST: $configKey ${request.httpMethod().name} ${request.url()}"

        if (requestLogLevel in listOf(RequestLogLevel.URL_BODY, RequestLogLevel.URL_BODY_HEADERS)) {
            requestLog += " BODY:[${getRequestBody(request)}]"
        }
        if (requestLogLevel == RequestLogLevel.URL_BODY_HEADERS) {
            requestLog += " HEADERS:[${getAllHeaders(request.headers())}]"
        }

        logger.info { requestLog }
    }

    override fun logAndRebufferResponse(
        configKey: String,
        logLevel: Level,
        response: Response,
        elapsedTime: Long
    ): Response {
        if (responseLogLevel == ResponseLogLevel.NONE) return response

        var responseLog = "RESPONSE: $configKey ${response.status()} (${elapsedTime}ms)"

        if (responseLogLevel !in listOf(ResponseLogLevel.STATUS_BODY, ResponseLogLevel.STATUS_BODY_HEADERS)) {
            logger.info { responseLog }
            return response
        }

        if (response.body() == null) {
            logger.info("$responseLog BODY:[]")
            return response
        }

        val bodyData = Util.toByteArray(response.body().asInputStream())

        if (bodyData.isEmpty()) {
            logger.info("$responseLog BODY:[]")
            return response
        }

        responseLog += " BODY:[${Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data")}]"

        val rebuiltResponse = response.toBuilder().body(bodyData).build()

        if (responseLogLevel != ResponseLogLevel.STATUS_BODY_HEADERS) {
            logger.info { responseLog }
            return rebuiltResponse
        }

        logger.info {  "$responseLog HEADERS:[${getAllHeaders(response.headers())}]" }

        return rebuiltResponse
    }

    override fun log(configKey: String?, format: String, vararg args: Any?) {
        logger.info(String.format(methodTag(configKey) + format, *args))
    }

    override fun logRetry(configKey: String, logLevel: Level) {
        logger.warn { "$configKey ---> RETRYING" }
    }

    private fun getAllHeaders(headers: Map<String, Collection<String>>): String {
        val result = StringBuilder()
        for (name in headers.keys) {
            result.append(" ").append(name).append(":[")
            for (value in headers[name] ?: emptyList()) {
                result.append(value)
            }
            result.append("]")
        }
        return result.toString().trim()
    }

    private fun getRequestBody(request: Request): String {
        if (request.body() == null) return ""
        return if (request.charset() != null) {
            String(request.body(), request.charset())
        } else {
            "Binary data"
        }
    }
}
package com.p2pbet.client.dataproviders.finnhub.api

import com.p2pbet.client.dataproviders.finnhub.api.model.CryptoCandlesResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

interface FinnHubApi {
    @GetMapping("/api/v1/crypto/candle")
    fun getCryptoCandles(
        @RequestParam symbol: String,
        @RequestParam resolution: String,
        @RequestParam from: Long,
        @RequestParam to: Long,
    ): CryptoCandlesResponse

    @GetMapping("/api/v1/stock/candle")
    fun getStockCandles(
        @RequestParam symbol: String,
        @RequestParam resolution: String,
        @RequestParam from: Long,
        @RequestParam to: Long,
    ): CryptoCandlesResponse
}
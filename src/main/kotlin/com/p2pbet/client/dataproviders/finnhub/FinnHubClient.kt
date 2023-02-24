package com.p2pbet.client.dataproviders.finnhub

import com.p2pbet.client.dataproviders.finnhub.api.FinnHubApi
import org.springframework.stereotype.Component

@Component
class FinnHubClient(
    private val finnHubApi: FinnHubApi,
) {
    fun getCryptoCandles(symbol: String, resolution: String, from: Long, to: Long) =
        finnHubApi.getCryptoCandles(symbol, resolution, from, to)

    fun getStockCandles(symbol: String, resolution: String, from: Long, to: Long) =
        finnHubApi.getStockCandles(symbol, resolution, from, to)

}
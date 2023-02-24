package com.p2pbet.finnhub

import com.p2pbet.client.dataproviders.finnhub.FinnHubClient
import com.p2pbet.client.dataproviders.finnhub.api.model.CryptoCandlesResponse
import com.p2pbet.p2pevent.controller.model.EventType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class FinnHubService(
    private val finnHubApi: FinnHubClient,
) {
    fun getCandles(type: EventType, symbol: String, resolution: String, from: Long, to: Long): CryptoCandlesResponse =
        finnHubApi.getCryptoCandles(symbol, resolution, from, to)


    fun getPriceByDate(type: EventType, symbol: String, date: LocalDateTime): BigDecimal = BigDecimal(
        getCandles(
            type = type,
            symbol = symbol,
            resolution = "1",
            from = (date.toEpochSecond(ZoneOffset.UTC) / 60) * 60,
            to = date.toEpochSecond(ZoneOffset.UTC)
        ).o!![0].toDouble()
    )
}

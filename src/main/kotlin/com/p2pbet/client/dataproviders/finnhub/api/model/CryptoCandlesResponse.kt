package com.p2pbet.client.dataproviders.finnhub.api.model

data class CryptoCandlesResponse (
    /* List of open prices for returned candles. */
    val o: List<Float>?,

    /* List of high prices for returned candles. */
    val h: List<Float>?,

    /* List of low prices for returned candles. */
    val l: List<Float>?,

    /* List of close prices for returned candles. */
    val c: List<Float>?,

    /* List of volume data for returned candles. */
    val v: List<Float>?,

    /* List of timestamp for returned candles. */
    val t: kotlin.collections.List<kotlin.Long>?,

    /* Status of the response. This field can either be ok or no_data. */
    val s: kotlin.String?
)


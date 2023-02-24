package com.p2pbet.bet.common.service

import com.p2pbet.bet.auction.entity.AuctionJoinEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ReportBuilder {
    private var dateTimeSecondFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
    private var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")!!
    fun buildActionJoinReport(
        data: List<AuctionJoinEntity>,
        target: String,
        reportDate: LocalDateTime,
        finalReport: Boolean,
    ): ByteArrayOutputStream = with(ByteArrayOutputStream()) {
        CSVPrinter(
            OutputStreamWriter(this),
            CSVFormat.EXCEL.withDelimiter(';')
        )
            .apply {
                printRecord(
                    "Exchange rate on Binance:",
                    target,
                    ""
                )
                printRecord(
                    "",
                    "",
                    ""
                )
            }
            .apply {
                printRecord(
                    "Report date:",
                    reportDate.format(dateTimeFormatter) + " UTC",
                    ""
                )
                printRecord(
                    "",
                    "",
                    ""
                )
            }
            .use {
                it.printRecord(
                    "Client",
                    "Bet date",
                    "Target"
                )
                var winnerTarget: String? = null
                data.forEachIndexed { i, data ->
                    if (i == 0) {
                        winnerTarget = data.targetValue
                    }

                    if (data.targetValue == winnerTarget && finalReport) {
                        it.printRecord(
                            data.client + " (Winner)",
                            data.createdDate.format(dateTimeSecondFormatter),
                            data.targetValue
                        )
                    } else {
                        it.printRecord(
                            data.client,
                            data.createdDate.format(dateTimeSecondFormatter),
                            data.targetValue
                        )
                    }

                }
            }
        this
    }
}
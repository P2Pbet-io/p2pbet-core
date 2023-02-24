package com.p2pbet.bet.auction.rest

import com.p2pbet.bet.auction.service.AuctionBetFilterService
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam


@Controller
class AuctionReportController(
    val auctionBetFilterService: AuctionBetFilterService,
) {
    @GetMapping(
        "/public/api/v1/auction/bet/{executionType}/{id}/report"
    )
    fun formReport(
        @PathVariable executionType: BetExecutionType,
        @PathVariable id: Long,
        @RequestParam value: String,
    ): ResponseEntity<InputStreamResource> {

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${id}_auction.csv")
            .body(
                InputStreamResource(
                    auctionBetFilterService.formReport(
                        betId = id,
                        target = value,
                        executionType = executionType
                    )
                )
            )
    }
}
package com.p2pbet.p2pevent.controller.model

data class AddEventRequest(
    val fullName: String,
    val name: String,
    val symbol: String,
    val type: EventType,
    val srcUrl: String,
)

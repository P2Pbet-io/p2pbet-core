package com.p2pbet.p2pevent.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.persistence.EntityNotFoundException

@RestControllerAdvice
class RestControllerAdvice {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException) = ResponseEntity.badRequest().body(e.message)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleIllegalArgument(e: EntityNotFoundException): ResponseEntity<String> = ResponseEntity.notFound().build()
}
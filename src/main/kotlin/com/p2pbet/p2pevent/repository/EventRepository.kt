package com.p2pbet.p2pevent.repository

import com.p2pbet.p2pevent.entity.BaseEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EventRepository: JpaRepository<BaseEvent, UUID>
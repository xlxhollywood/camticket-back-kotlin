package org.example.camticketkotlin.repository

import org.example.camticketkotlin.domain.PerformancePost
import org.example.camticketkotlin.domain.TicketOption
import org.springframework.data.jpa.repository.JpaRepository

interface TicketOptionRepository : JpaRepository<TicketOption, Long> {
    fun findByPerformancePost(performancePost: PerformancePost): List<TicketOption>
    fun deleteAllByPerformancePost(performancePost: PerformancePost)
}

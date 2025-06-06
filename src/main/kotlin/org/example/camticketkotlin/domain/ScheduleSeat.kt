package org.example.camticketkotlin.domain

import jakarta.persistence.*

@Entity
class ScheduleSeat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var seatCode: String,  // e.g., "A1", "B3"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SeatStatus,  // AVAILABLE / UNAVAILABLE / RESERVED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_schedule_id", nullable = false)
    var performanceSchedule: PerformanceSchedule
)

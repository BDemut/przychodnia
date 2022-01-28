package com.example.demo.database.entities

import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
open class Reservation (
    @Column(nullable = false)
    open var dateFrom: LocalDate? = null,

    @Column(nullable = false)
    open var hourFrom: LocalTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var room: Room? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var doctor: Doctor? = null,

    @Column(nullable = false)
    open var until: LocalTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
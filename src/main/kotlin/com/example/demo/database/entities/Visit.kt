package com.example.demo.database.entities

import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
open class Visit (
    @Column(nullable = false)
    open var date: LocalDate? = null,

    @Column(nullable = false)
    open var time: LocalTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var doctor: Doctor? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var patient: Patient? = null,

    @ManyToMany(targetEntity = Service::class, fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var services: List<Service>? = null,

    @Column
    open var takenPlace: Int? = null,

    @Column(length = 5000)
    open var note: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
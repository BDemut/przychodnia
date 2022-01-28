package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Room (
    @Id
    @Column(nullable = false)
    open var number: Int? = null,

    @ManyToMany(targetEntity = Equipment::class, fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var equipment: List<Equipment>? = null,

    @ManyToMany(targetEntity = Clinic::class, mappedBy="rooms", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var clinics: List<Clinic>? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,
)
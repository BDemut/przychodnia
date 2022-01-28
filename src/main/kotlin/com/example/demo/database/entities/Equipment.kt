package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Equipment (
    @Column(nullable = false, length = 30)
    open var name: String? = null,

    @ManyToMany(targetEntity = Room::class, mappedBy = "equipment", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var rooms: List<Room>? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
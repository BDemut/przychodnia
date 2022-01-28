package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Clinic (

    @Column(nullable = false, length = 50)
    open var name: String? = null,

    @ManyToMany(targetEntity = Room::class, fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var rooms: List<Room>? = null,

    @ManyToMany(targetEntity = Doctor::class, mappedBy = "clinics" ,fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var doctors: List<Doctor>? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
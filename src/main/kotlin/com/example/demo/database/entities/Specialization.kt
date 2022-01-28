package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Specialization (
    @Column(nullable = false, length = 20)
    open var name: String? = null,

    @ManyToMany(targetEntity = Doctor::class, mappedBy = "specializations", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var doctors: List<Doctor>? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
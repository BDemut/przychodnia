package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Degree(
    @Column(nullable = false, length = 10)
    open var short: String? = null,

    @Column(nullable = false, length = 50)
    open var full: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Patient(
    @Column(nullable = false, length = 11)
    open var pesel: String? = null,

    @Column(nullable = false, length = 20)
    open var fname: String? = null,

    @Column(nullable = false, length = 30)
    open var lname: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
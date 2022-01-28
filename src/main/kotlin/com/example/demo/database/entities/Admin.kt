package com.example.demo.database.entities

import javax.persistence.*


@Entity
open class Admin (
    @Column(nullable = false, length = 11)
    open var pesel: String? = null,

    @Column(nullable = false, length = 20)
    open var fname: String? = null,

    @Column(nullable = false, length = 30)
    open var lname: String? = null,

    @Column(nullable = false, length = 30)
    open var password: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
    ) {

    fun toTableName(): String = "$fname $lname"
}


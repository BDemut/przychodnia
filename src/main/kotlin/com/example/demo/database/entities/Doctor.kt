package com.example.demo.database.entities

import javax.persistence.*

@Entity
open class Doctor (
    @Column(nullable = false, length = 11)
    open var pesel: String? = null,

    @Column(nullable = false, length = 20)
    open var fname: String? = null,

    @Column(nullable = false, length = 30)
    open var lname: String? = null,

    @Column(nullable = false, length = 30)
    open var password: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var degree: Degree? = null,

    @ManyToMany(targetEntity = Specialization::class, fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var specializations: List<Specialization>? = null,

    @ManyToMany(targetEntity = Clinic::class, fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var clinics: List<Clinic>? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var adder: Admin? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
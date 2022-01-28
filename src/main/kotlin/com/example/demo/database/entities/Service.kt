package com.example.demo.database.entities

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
open class Service (
    @Column(nullable = false, length = 50)
    open var name: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @OnDelete(action = OnDeleteAction.CASCADE)
    open var doctor: Doctor? = null,

    @Column(nullable = false)
    open var price: Double,

    @Column(nullable = false)
    open var duration: Int? = null,

    @ManyToMany(targetEntity = Visit::class, mappedBy="services", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    open var visits: List<Visit>? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Int? = null
)
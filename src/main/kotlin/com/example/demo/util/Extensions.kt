package com.example.demo.util

import com.example.demo.database.entities.Degree
import javax.persistence.EntityManager

fun <T> EntityManager.findAll(entityName: String): List<T> = createQuery("Select t from $entityName t")
        .resultList as List<T>

fun <T> EntityManager.findOne(entityName: String, columnName: String, parameter: String): T =
        createQuery("Select t from $entityName t where $columnName = '$parameter'").resultList.first() as T

const val ADMIN = "Admin"
const val CLINIC = "Clinic"
const val DEGREE = "Degree"
const val DOCTOR = "Doctor"
const val EQUIPMENT = "Equipment"
const val PATIENT = "Patient"
const val RESERVATION = "Reservation"
const val ROOM ="Room"
const val SERVICE = "Service"
const val SPECIALIZATION = "Specialization"
const val VISIT = "Visit"
package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

@SpringBootApplication
class MedCentreApplication 

fun main(args: Array<String>) {
	runApplication<MedCentreApplication>(*args)
}

var session: Session? = null
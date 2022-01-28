package com.example.demo.controllers

import com.example.demo.ForbiddenException
import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Reservation
import com.example.demo.database.entities.Visit
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.CrudType
import com.example.demo.session
import com.example.demo.util.VISIT
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import toHtmlTable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Controller
class DoctorVisitsController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/visitsDoctor")
    fun visitsDoctor(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.ADMIN) return "error"
        if (search == null) {
            var query = QueryBuilder(VISIT, "services")
                .addEquals("doctor.id", session!!.user!!.id.toString())
                .build()
            val allResults: List<Visit?> = entityManager!!.createQuery(query).resultList as List<Visit>
            model["table"] = allResults.toSet().toList().toHtmlTable(true)
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Visit>()
            var query = QueryBuilder(VISIT, "services")
                .addEquals("doctor.id", session!!.user!!.id.toString())
                .build()
            (entityManager!!.createQuery(query).resultList as List<Visit>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.forEach { search ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    try {
                        if (value.date == LocalDate.parse(search)) drop = false
                    } catch (_: DateTimeParseException) {
                    }
                    try {
                        if (value.time == LocalTime.parse(search)) drop = false
                    } catch (_: DateTimeParseException) {
                    }
                    if (value.patient!!.fname!!.contains(search)) drop = false
                    if (value.patient!!.lname!!.contains(search)) drop = false
                    if (value.patient!!.pesel!!.contains(search)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable(true)
        }

        return "doctorvisits"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsDoctorPost"])
    @Transactional
    fun visitsPost(@RequestBody data: Visit, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {

            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Visit::class.java, data.id))
            }
            CrudType.EDIT -> {
                val dbEntity = entityManager!!.find(Visit::class.java, data.id)
                dbEntity.note = data.note
                entityManager.merge(dbEntity)
            }
        }
        return "good"
    }
}
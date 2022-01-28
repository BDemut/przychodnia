package com.example.demo.controllers

import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.*
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.CrudType
import com.example.demo.session
import com.example.demo.util.*
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
class AdminVisitsController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/visitsAdmin")
    fun visitsAdmin(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Visit?> = entityManager!!.findAll(VISIT)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Visit>()
            var query = QueryBuilder(VISIT, "services")
                .addEquals("time", searchList[0])
                .addEquals("date", searchList[0])
                .addLike("doctor.fname", searchList[0])
                .addLike("doctor.lname", searchList[0])
                .addLike("doctor.pesel", searchList[0])
                .addLike("patient.fname", searchList[0])
                .addLike("patient.lname", searchList[0])
                .addLike("patient.pesel", searchList[0])
                .addLike("name", searchList[0], "services")
                .build()
            (entityManager!!.createQuery(query).resultList as List<Visit>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.subList(1, searchList.size).forEach { search ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    try {
                        if (value.date == LocalDate.parse(search)) drop = false
                    } catch (_ : DateTimeParseException) {}
                    try {
                        if (value.time == LocalTime.parse(search)) drop = false
                    } catch (_ : DateTimeParseException) {}
                    if (value.doctor!!.fname!!.contains(search)) drop = false
                    if (value.doctor!!.lname!!.contains(search)) drop = false
                    if (value.doctor!!.pesel!!.contains(search)) drop = false
                    if (value.patient!!.fname!!.contains(search)) drop = false
                    if (value.patient!!.lname!!.contains(search)) drop = false
                    if (value.patient!!.pesel!!.contains(search)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }

        return "adminvisits"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsAdminPost"])
    @Transactional
    fun visitsPost(@RequestBody data: Visit, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {

            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Visit::class.java, data.id))
            }
            CrudType.EDIT -> {

            }
        }
        return "good"
    }
}
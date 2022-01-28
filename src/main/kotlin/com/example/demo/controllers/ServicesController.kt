package com.example.demo.controllers

import com.example.demo.ForbiddenException
import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Doctor
import com.example.demo.database.entities.Service
import com.example.demo.database.entities.Specialization
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.CrudType
import com.example.demo.session
import com.example.demo.util.SERVICE
import com.example.demo.util.SPECIALIZATION
import com.example.demo.util.findAll
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import toHtmlTable
import java.lang.IllegalArgumentException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Controller
class ServicesController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/services")
    fun equipment(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.ADMIN) return "error"
        if (search == null) {
            var query = QueryBuilder(SERVICE)
                .addEquals("doctor.id", session!!.user!!.id.toString())
                .build()
            val allResults: List<Service?> = entityManager!!.createQuery(query).resultList as List<Service>
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Service>()
            var query = QueryBuilder(SERVICE)
                .addEquals("doctor.id", session!!.user!!.id.toString())
                .build()
            (entityManager!!.createQuery(query).resultList as List<Service>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.forEach { it ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.name!!.contains(it)) drop = false
                    if (value.id == it.toIntOrNull()) drop = false
                    if (value.duration == it.toIntOrNull()) drop = false
                    if (value.price == it.toDoubleOrNull()) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }
        return "doctorservices"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/servicesPost"])
    @Transactional
    fun servicePost(@RequestBody data: Service, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.name!!.isBlank())
                    throw IllegalArgumentException()
                data.doctor = entityManager!!.getReference(Doctor::class.java, session!!.user!!.id)
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Service::class.java, data.id))
            }
            CrudType.EDIT -> {
                val dbEntity = entityManager!!.find(Service::class.java, data.id)
                if (data.name!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwę!")
                data.doctor = dbEntity.doctor
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
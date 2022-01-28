package com.example.demo.controllers

import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Degree
import com.example.demo.database.entities.Specialization
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.CrudType
import com.example.demo.session
import com.example.demo.util.DEGREE
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
class DegreesController {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/degrees")
    fun equipment(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Degree?> = entityManager!!.findAll(DEGREE)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Degree>()
            var query = QueryBuilder(DEGREE)
                .addEquals("id", searchList[0])
                .addLike("short", searchList[0])
                .addLike("full", searchList[0])
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Degree>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.subList(1, searchList.size).forEach { it ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.short!!.contains(it)) drop = false
                    if (value.full!!.contains(it)) drop = false
                    if (value.id == it.toIntOrNull()) drop = false
                    if (value.adder!!.fname!!.contains(it)) drop = false
                    if (value.adder!!.lname!!.contains(it)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }
        return "admindegrees"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/degreesPost"])
    @Transactional
    fun degreesPost(@RequestBody data: Degree, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.short!!.isBlank())
                    throw IllegalArgumentException()
                if (data.full!!.isBlank())
                    throw IllegalArgumentException()
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Degree::class.java, data.id))
            }
            CrudType.EDIT -> {
                if (data.short!!.isBlank())
                    throw IllegalArgumentException()
                if (data.full!!.isBlank())
                    throw IllegalArgumentException()
                val dbEntity = entityManager!!.find(Degree::class.java, data.id)
                data.adder = dbEntity.adder
                if (data.short.isNullOrBlank()) data.short = dbEntity.short
                if (data.full.isNullOrBlank()) data.full = dbEntity.full
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
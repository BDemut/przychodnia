package com.example.demo.controllers

import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Equipment
import com.example.demo.database.entities.Specialization
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.CrudType
import com.example.demo.session
import com.example.demo.util.EQUIPMENT
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
class SpecializationController {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/specializations")
    fun equipment(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Specialization?> = entityManager!!.findAll(SPECIALIZATION)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Specialization>()
            var query = QueryBuilder(SPECIALIZATION)
                .addEquals("id", searchList[0])
                .addLike("name", searchList[0])
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Specialization>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.subList(1, searchList.size).forEach { it ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.name!!.contains(it)) drop = false
                    if (value.id == it.toIntOrNull()) drop = false
                    if (value.adder!!.fname!!.contains(it)) drop = false
                    if (value.adder!!.lname!!.contains(it)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }
        return "adminspecializations"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/specializationsPost"])
    @Transactional
    fun specializationPost(@RequestBody data: Specialization, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.name!!.isBlank())
                    throw IllegalArgumentException()
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Specialization::class.java, data.id))
            }
            CrudType.EDIT -> {
                val dbEntity = entityManager!!.find(Specialization::class.java, data.id)
                if (data.name!!.isBlank())
                    throw IllegalArgumentException()
                data.adder = dbEntity.adder
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
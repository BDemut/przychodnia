package com.example.demo.controllers

import com.example.demo.ForbiddenException
import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Equipment
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
import java.lang.IllegalArgumentException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Controller
class EquipmentController {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/equipment")
    fun equipment(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Equipment?> = entityManager!!.findAll(EQUIPMENT)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Equipment>()
            var query = QueryBuilder(EQUIPMENT)
                .addEquals("id", searchList[0])
                .addLike("name", searchList[0])
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Equipment>).forEach{
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
        return "adminequipment"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/equipmentPost"])
    @Transactional
    fun equipmentPost(@RequestBody data: Equipment, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.name!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwę!")
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                entityManager!!.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Equipment::class.java, data.id))
            }
            CrudType.EDIT -> {
                if (data.name!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwę!")
                val dbEntity = entityManager!!.find(Equipment::class.java, data.id)
                data.adder = dbEntity.adder
                data.name = dbEntity.name
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
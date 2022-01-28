package com.example.demo.controllers

import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Equipment
import com.example.demo.database.entities.Room
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.CrudType
import com.example.demo.session
import com.example.demo.util.EQUIPMENT
import com.example.demo.util.ROOM
import com.example.demo.util.findAll
import com.example.demo.util.toHtmlOptions
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import toHtmlTable
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Controller
class RoomsController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/rooms")
    fun rooms(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Room?> = entityManager!!.findAll(ROOM)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Room>()
            var query = QueryBuilder(ROOM, "equipment")
                .addEquals("number", searchList[0])
                .addLike("name", searchList[0], "equipment")
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Room>).forEach {
                filteredResults[it.number!!] = it
            }

            searchList.subList(1, searchList.size).forEach { it ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.equipment?.any { it.name!!.contains(search) } == true) drop = false
                    if (value.number == it.toIntOrNull()) drop = false
                    if (value.adder!!.fname!!.contains(it)) drop = false
                    if (value.adder!!.lname!!.contains(it)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }

        model["equipmentOptions"] = entityManager.findAll<Equipment>(EQUIPMENT).map { it.name!! }.toHtmlOptions()

        return "adminrooms"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/roomsPost"])
    @Transactional
    fun roomsPost(@RequestBody data: Room, @RequestParam(required = true) crudType: CrudType): String {
        when (crudType) {
            CrudType.ADD -> {
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                data.equipment = data.equipment?.map {
                    val eqDbEntity = (entityManager.createQuery(
                        QueryBuilder(EQUIPMENT).addEquals("name", it.name!!).build()
                    ).resultList as List<Equipment>).first()
                    entityManager.getReference(Equipment::class.java, eqDbEntity.id)
                }
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Room::class.java, data.number))
            }
            CrudType.EDIT -> {
                val dbEntity = entityManager!!.find(Room::class.java, data.number)
                data.equipment = data.equipment?.map {
                    val eqDbEntity = (entityManager.createQuery(
                        QueryBuilder(EQUIPMENT).addEquals("name", it.name!!).build()
                    ).resultList as List<Equipment>).first()
                    entityManager.getReference(Equipment::class.java, eqDbEntity.id)
                }
                data.adder = dbEntity.adder
                entityManager.merge(data)
            }
        }
        return "good"
    }

}
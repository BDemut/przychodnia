package com.example.demo.controllers

import com.example.demo.database.QueryBuilder
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Clinic
import com.example.demo.database.entities.Equipment
import com.example.demo.database.entities.Room
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
class ClinicsController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/clinics")
    fun clinics(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Clinic?> = entityManager!!.findAll(CLINIC)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Clinic>()
            var query = QueryBuilder(CLINIC, "rooms")
                .addEquals("id", searchList[0])
                .addEquals("number", searchList[0], "rooms")
                .addLike("name", searchList[0])
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Clinic>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.subList(1, searchList.size).forEach { search ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.rooms?.any{ it.number!! == search.toIntOrNull() } == true) drop = false
                    if (value.id == search.toIntOrNull()) drop = false
                    if (value.name!!.contains(search)) drop = false
                    if (value.adder!!.fname!!.contains(search)) drop = false
                    if (value.adder!!.lname!!.contains(search)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }

        model["roomOptions"] = entityManager.findAll<Room>(ROOM).map { it.number!!.toString() }.toHtmlOptions()

        return "adminclinics"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/clinicsPost"])
    @Transactional
    fun clinicsPost(@RequestBody data: Clinic, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.name!!.isBlank())
                    throw IllegalArgumentException()
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                data.rooms = data.rooms?.map {
                    entityManager.getReference(Room::class.java, it.number)
                }
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Clinic::class.java, data.id))
            }
            CrudType.EDIT -> {
                if (data.name!!.isBlank())
                    throw IllegalArgumentException()
                val dbEntity = entityManager!!.find(Clinic::class.java, data.id)
                data.rooms = data.rooms?.map {
                    entityManager.getReference(Room::class.java, it.number)
                }
                data.adder = dbEntity.adder
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
package com.example.demo.controllers

import com.example.demo.ForbiddenException
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
import java.lang.IllegalArgumentException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Controller
class AdminsController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/admins")
    fun equipment(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Admin?> = entityManager!!.findAll(ADMIN)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Admin>()
            var query = QueryBuilder(ADMIN)
                .addEquals("id", searchList[0])
                .addLike("fname", searchList[0])
                .addLike("lname", searchList[0])
                .addLike("pesel", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Admin>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.subList(1, searchList.size).forEach { search ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.fname!!.contains(search)) drop = false
                    if (value.lname!!.contains(search)) drop = false
                    if (value.pesel!!.contains(search)) drop = false
                    if (value.id == search.toIntOrNull()) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }

        return "adminadmins"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/adminsPost"])
    @Transactional
    fun adminPost(@RequestBody data: Admin, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.pesel!!.length != 11 || data.pesel!!.any { !it.isDigit() })
                    throw ForbiddenException("Pesel musi składać się z 11 cyfr")
                val query = QueryBuilder(ADMIN).addEquals("pesel", data.pesel!!).build()
                if ((entityManager!!.createQuery(query).resultList).isNotEmpty())
                    throw ForbiddenException("Istnieje już administrator o podanym PESELu!")
                if (data.fname!!.isBlank())
                    throw ForbiddenException("Musisz podać imię!")
                if (data.lname!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwisko!")
                if (data.password!!.isBlank())
                    throw ForbiddenException("Musisz podać hasło!")
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Admin::class.java, data.id))
            }
            CrudType.EDIT -> {
                if (data.pesel!!.length != 11 || data.pesel!!.any { !it.isDigit() })
                    throw ForbiddenException("Pesel musi składać się z 11 cyfr")
                if (data.fname!!.isBlank())
                    throw ForbiddenException("Musisz podać imię!")
                if (data.lname!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwisko!")
                if (data.password!!.isBlank())
                    throw ForbiddenException("Musisz podać hasło!")
                val dbEntity = entityManager!!.find(Admin::class.java, data.id)
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
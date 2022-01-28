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
class DoctorsController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/doctors")
    fun equipment(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Doctor?> = entityManager!!.findAll(DOCTOR)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Doctor>()
            var query = QueryBuilder(DOCTOR, "specializations", "clinics")
                .addEquals("id", searchList[0])
                .addLike("fname", searchList[0])
                .addLike("lname", searchList[0])
                .addLike("pesel", searchList[0])
                .addLike("degree.short", searchList[0])
                .addLike("name", searchList[0], "specializations")
                .addLike("name", searchList[0], "clinics")
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Doctor>).forEach{
                filteredResults[it.id!!] = it
            }

            searchList.subList(1, searchList.size).forEach { search ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.fname!!.contains(search)) drop = false
                    if (value.lname!!.contains(search)) drop = false
                    if (value.pesel!!.contains(search)) drop = false
                    if (value.degree?.short?.contains(search) == true) drop = false
                    if (value.specializations?.any{ it.name!!.contains(search) } == true) drop = false
                    if (value.clinics?.any{ it.name!!.contains(search) } == true) drop = false
                    if (value.id == search.toIntOrNull()) drop = false
                    if (value.adder!!.fname!!.contains(search)) drop = false
                    if (value.adder!!.lname!!.contains(search)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }

        model["specializationsOptions"] = entityManager.findAll<Specialization>(SPECIALIZATION).map { it.name!! }.toHtmlOptions()

        model["clinicsOptions"] = entityManager.findAll<Clinic>(CLINIC).map { it.name!! }.toHtmlOptions()

        model["degreesOptions"] = entityManager.findAll<Degree>(DEGREE).map { it.short!! }.toHtmlOptions()

        return "admindoctors"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/doctorsPost"])
    @Transactional
    fun doctorPost(@RequestBody data: Doctor, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.pesel!!.length != 11 || data.pesel!!.any { !it.isDigit() })
                    throw ForbiddenException("Pesel musi składać się z 11 cyfr")
                val query = QueryBuilder(DOCTOR).addEquals("pesel", data.pesel!!).build()
                if ((entityManager!!.createQuery(query).resultList).isNotEmpty())
                    throw ForbiddenException("Istnieje już lekarz o podanym PESELu!")
                if (data.fname!!.isBlank())
                    throw ForbiddenException("Musisz podać imię!")
                if (data.lname!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwisko!")
                if (data.password!!.isBlank())
                    throw ForbiddenException("Musisz podać hasło!")
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                data.clinics = data.clinics?.map {
                    entityManager.getReference(
                        Clinic::class.java,
                        entityManager.findOne<Clinic>(CLINIC, "name", it.name!!).id
                    )
                }
                data.specializations = data.specializations?.map {
                    entityManager.getReference(
                        Specialization::class.java,
                        entityManager.findOne<Specialization>(SPECIALIZATION, "name", it.name!!).id
                    )
                }
                data.degree = entityManager.getReference(
                    Degree::class.java,
                    entityManager.findOne<Degree>(DEGREE, "short", data.degree!!.short!!).id
                )
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Doctor::class.java, data.id))
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
                val dbEntity = entityManager!!.find(Doctor::class.java, data.id)
                data.adder = dbEntity.adder
                data.clinics = data.clinics?.map {
                    entityManager.getReference(
                        Clinic::class.java,
                        entityManager.findOne<Clinic>(CLINIC, "name", it.name!!).id
                    )
                }
                data.specializations = data.specializations?.map {
                    entityManager.getReference(
                        Specialization::class.java,
                        entityManager.findOne<Specialization>(SPECIALIZATION, "name", it.name!!).id
                    )
                }
                data.degree = entityManager.getReference(
                    Degree::class.java,
                    entityManager.findOne<Degree>(DEGREE, "short", data.degree!!.short!!).id
                )
                entityManager.merge(data)
            }
        }
        return "good"
    }
}
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Controller
class ReservationController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/doctorReservations")
    fun doctorReservations(model: Model, @RequestParam(required = false) search: String?, @RequestParam(required = false) rooms: Boolean?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.ADMIN) return "error"
        if (search == null) {
            var query = QueryBuilder(RESERVATION)
                .addEquals("doctor.id", session!!.user!!.id.toString())
                .build()
            val allResults: List<Reservation?> = entityManager!!.createQuery(query).resultList as List<Reservation>

            model["table"] = allResults.toHtmlTable()
            model["roomsTable"] = entityManager!!.findAll<Room>(ROOM).toHtmlTable(false)
        } else {
            if (rooms == true) {
                var query = QueryBuilder(RESERVATION)
                    .addEquals("doctor.id", session!!.user!!.id.toString())
                    .build()
                val allResults: List<Reservation?> = entityManager!!.createQuery(query).resultList as List<Reservation>

                model["table"] = allResults.toHtmlTable()

                val searchList = search.split(' ')
                val filteredResults = HashMap<Int, Room>()
                query = QueryBuilder(ROOM, "equipment")
                    .addEquals("number", searchList[0])
                    .addLike("name", searchList[0], "equipment")
                    .addLike("adder.fname", searchList[0])
                    .addLike("adder.lname", searchList[0])
                    .build()
                (entityManager!!.createQuery(query).resultList as List<Room>).forEach{
                    filteredResults[it.number!!] = it
                }

                searchList.subList(1, searchList.size).forEach { subsearch ->
                    val keysToRemove = mutableListOf<Int>()
                    filteredResults.forEach { (key, value) ->
                        var drop = true
                        if (value.equipment?.any{ it.name!!.contains(subsearch) } == true) drop = false
                        if (value.number == subsearch.toIntOrNull()) drop = false
                        if (value.adder!!.fname!!.contains(subsearch)) drop = false
                        if (value.adder!!.lname!!.contains(subsearch)) drop = false
                        if (drop) keysToRemove.add(key)
                    }
                    keysToRemove.forEach { filteredResults.remove(it) }
                }

                model["roomsTable"] = filteredResults.values.toList().toHtmlTable()
            } else {
                val searchList = search.split(' ')
                val filteredResults = HashMap<Int, Reservation>()
                var query = QueryBuilder(RESERVATION)
                    .addEquals("doctor.id", session!!.user!!.id.toString())
                    .build()
                (entityManager!!.createQuery(query).resultList as List<Reservation>).forEach {
                    filteredResults[it.id!!] = it
                }

                searchList.forEach { subsearch ->
                    val keysToRemove = mutableListOf<Int>()
                    filteredResults.forEach { (key, value) ->
                        var drop = true
                        if (value.room?.number == subsearch.toIntOrNull()) drop = false
                        if (value.id == subsearch.toIntOrNull()) drop = false
                        try {
                            if (value.dateFrom == LocalDate.parse(subsearch)) drop = false
                        } catch (_ : DateTimeParseException) {}
                        try {
                            if (value.hourFrom == LocalTime.parse(subsearch)) drop = false
                        } catch (_ : DateTimeParseException) {}
                        try {
                            if (value.until == LocalTime.parse(subsearch)) drop = false
                        } catch (_ : DateTimeParseException) {}
                        if (drop) keysToRemove.add(key)
                    }
                    keysToRemove.forEach { filteredResults.remove(it) }
                }

                model["table"] = filteredResults.values.toList().toHtmlTable()

                model["roomsTable"] = entityManager!!.findAll<Room>(ROOM).toHtmlTable(false)
            }
        }

        model["roomsOptions"] = entityManager!!.findAll<Room>(ROOM).map{ it.number.toString() }.toHtmlOptions()

        return "doctorreservations"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/doctorReservationsPost"])
    @Transactional
    fun doctorReservations(@RequestBody data: Reservation, @RequestParam(required = true) crudType: CrudType): String {
        when(crudType) {
            CrudType.ADD -> {
                if (data.dateFrom == null)
                    throw ForbiddenException("Musisz podać datę!")
                if (data.hourFrom == null)
                    throw ForbiddenException("Musisz podać godzinę od!")
                if (data.until == null)
                    throw ForbiddenException("Musisz podać godzinę do!")
                data.doctor = entityManager!!.getReference(Doctor::class.java, session!!.user!!.id)
                data.checkIfValid()
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Reservation::class.java, data.id))
            }
            CrudType.EDIT -> {
                if (data.dateFrom == null)
                    throw ForbiddenException("Musisz podać datę!")
                if (data.hourFrom == null)
                    throw ForbiddenException("Musisz podać godzinę od!")
                if (data.until == null)
                    throw ForbiddenException("Musisz podać godzinę do!")
                val dbEntity = entityManager!!.find(Reservation::class.java, data.id)
                data.doctor = dbEntity.doctor
                data.checkIfValid()
                entityManager.merge(data)
            }
        }
        return "good"
    }

    private fun Reservation.checkIfValid() {
        if (hourFrom!!.isAfter(until))
            throw ForbiddenException("Podaj poprawny przedział czasowy!")
        if (dateFrom!!.isBefore(LocalDate.now()))
            throw ForbiddenException("Nie można utworzyć rezerwacji w przeszłości!")
        if (dateFrom!!.isEqual(LocalDate.now()) && hourFrom!!.isBefore(LocalTime.now()))
            throw ForbiddenException("Nie można utworzyć rezerwacji w przeszłości!")
        entityManager!!.findAll<Reservation>(RESERVATION).forEach { r ->
            if (dateFrom == r.dateFrom && room!!.number == r.room!!.number) {
                if (r.hourFrom!!.isAfter(hourFrom) && r.hourFrom!!.isBefore(until))
                    throw ForbiddenException("Ten gabinet jest już zarezerwowany w tym czasie!")
                if (r.until!!.isAfter(hourFrom) && r.until!!.isBefore(until))
                    throw ForbiddenException("Ten gabinet jest już zarezerwowany w tym czasie!")
            }
        }
    }

    @GetMapping("/reservations")
    fun reservations(model: Model, @RequestParam(required = false) search: String?, @RequestParam(required = false) rooms: Boolean?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (search == null) {
            val allResults: List<Reservation?> = entityManager!!.findAll(RESERVATION)
            model["table"] = allResults.toHtmlTable(true)
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Reservation>()
            var query = QueryBuilder(RESERVATION)
                .addEquals("id", searchList[0])
                .addEquals("room.number", searchList[0])
                .addEquals("dateFrom", searchList[0])
                .addEquals("hourFrom", searchList[0])
                .addEquals("until", searchList[0])
                .addLike("doctor.fname", searchList[0])
                .addLike("doctor.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Reservation>).forEach {
                filteredResults[it.id!!] = it
            }

            searchList.forEach { subsearch ->
                val keysToRemove = mutableListOf<Int>()
                filteredResults.forEach { (key, value) ->
                    var drop = true
                    if (value.room?.number == subsearch.toIntOrNull()) drop = false
                    if (value.id == subsearch.toIntOrNull()) drop = false
                    try {
                        if (value.dateFrom == LocalDate.parse(subsearch)) drop = false
                    } catch (_ : DateTimeParseException) {}
                    try {
                        if (value.hourFrom == LocalTime.parse(subsearch)) drop = false
                    } catch (_ : DateTimeParseException) {}
                    try {
                        if (value.until == LocalTime.parse(subsearch)) drop = false
                    } catch (_ : DateTimeParseException) {}
                    if (value.doctor!!.fname!!.contains(subsearch)) drop = false
                    if (value.doctor!!.lname!!.contains(subsearch)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable(true)
        }
        return "adminreservations"
    }
}
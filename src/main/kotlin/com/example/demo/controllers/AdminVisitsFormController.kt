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
import java.time.LocalTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.time.TimeSource

@Controller
class AdminVisitsFormController {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    private var visit: Visit = Visit()

    @GetMapping("/visitsAdminForm")
    fun visitsAdmin(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        visit = Visit()
        if (search == null) {
            val allResults: List<Patient?> = entityManager!!.findAll(PATIENT)
            model["table"] = allResults.toHtmlTable()
        } else {
            val searchList = search.split(' ')
            val filteredResults = HashMap<Int, Patient>()
            var query = QueryBuilder(PATIENT)
                .addEquals("id", searchList[0])
                .addLike("fname", searchList[0])
                .addLike("lname", searchList[0])
                .addLike("pesel", searchList[0])
                .addLike("adder.fname", searchList[0])
                .addLike("adder.lname", searchList[0])
                .build()
            (entityManager!!.createQuery(query).resultList as List<Patient>).forEach {
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
                    if (value.adder!!.fname!!.contains(search)) drop = false
                    if (value.adder!!.lname!!.contains(search)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable()
        }
        return "adminvisits1"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsAdminFormPost"])
    @Transactional
    fun patientPost(@RequestBody data: Patient, @RequestParam(required = true) crudType: CrudType): String {
        when (crudType) {
            CrudType.ADD -> {
                if (data.pesel!!.length != 11 || data.pesel!!.any { !it.isDigit() })
                    throw ForbiddenException("Pesel musi składać się z 11 cyfr")
                val query = QueryBuilder(PATIENT).addEquals("pesel", data.pesel!!).build()
                if ((entityManager!!.createQuery(query).resultList).isNotEmpty())
                    throw ForbiddenException("Istnieje już pacjent o podanym PESELu!")
                if (data.fname!!.isBlank())
                    throw ForbiddenException("Musisz podać imię!")
                if (data.lname!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwisko!")
                data.adder = entityManager!!.getReference(Admin::class.java, session!!.user!!.id)
                entityManager.persist(data)
            }
            CrudType.REMOVE -> {
                entityManager!!.remove(entityManager.getReference(Patient::class.java, data.id))
            }
            CrudType.EDIT -> {
                if (data.pesel!!.length != 11 || data.pesel!!.any { !it.isDigit() })
                    throw ForbiddenException("Pesel musi składać się z 11 cyfr")
                if (data.fname!!.isBlank())
                    throw ForbiddenException("Musisz podać imię!")
                if (data.lname!!.isBlank())
                    throw ForbiddenException("Musisz podać nazwisko!")
                val dbEntity = entityManager!!.find(Patient::class.java, data.id)
                data.adder = dbEntity.adder
                entityManager.merge(data)
            }
        }
        return "good"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsAdminChoosePatient"])
    fun doctor(@RequestBody data: Patient): String {
        visit.patient = entityManager!!.find(Patient::class.java, data.id)
        return "good"
    }

    @GetMapping("/visitsAdminForm2")
    fun visitsAdmin2(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        if (search == null) {
            val allResults: List<Doctor?> = entityManager!!.findAll(DOCTOR)
            model["table"] = allResults.toHtmlTable(true)
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
            (entityManager!!.createQuery(query).resultList as List<Doctor>).forEach {
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
                    if (value.specializations?.any { it.name!!.contains(search) } == true) drop = false
                    if (value.clinics?.any { it.name!!.contains(search) } == true) drop = false
                    if (value.id == search.toIntOrNull()) drop = false
                    if (value.adder!!.fname!!.contains(search)) drop = false
                    if (value.adder!!.lname!!.contains(search)) drop = false
                    if (drop) keysToRemove.add(key)
                }
                keysToRemove.forEach { filteredResults.remove(it) }
            }

            model["table"] = filteredResults.values.toList().toHtmlTable(true)
        }
        return "adminvisits2"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsAdminChooseDoctor"])
    fun doctor(@RequestBody data: Doctor): String {
        visit.doctor = entityManager!!.find(Doctor::class.java, data.id)
        return "good"
    }

    @GetMapping("/visitsAdminForm3")
    fun visitsAdmin3(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"
        model["servicesOptions"] = entityManager!!.findAll<Service>(SERVICE)
            .filter { it.doctor!!.id == visit.doctor!!.id }
            .map { it.name!! }.toHtmlOptions()
        model["dateOptions"] = entityManager!!.findAll<Reservation>(RESERVATION)
            .filter { it.doctor!!.id == visit.doctor!!.id }
            .map { it.dateFrom.toString() }.toHtmlOptions()
        return "adminvisits3"
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsAdminChooseDateAndServices"])
    fun dateAndServices(@RequestBody data: Visit): String {
        visit.date = data.date
        visit.services = data.services?.map {
            entityManager!!.getReference(
                Service::class.java,
                entityManager.findOne<Service>(SERVICE, "name", it.name!!).id
            )
        }
        return "good"
    }

    @GetMapping("/visitsAdminForm4")
    fun visitsAdmin4(model: Model, @RequestParam(required = false) search: String?): String {
        if (session?.user?.credentialType == null || session?.user?.credentialType == CredentialType.ERROR) return "login"
        if (session?.user?.credentialType == CredentialType.DOCTOR) return "error"

        val reservations = entityManager!!.findAll<Reservation>(RESERVATION)
            .filter { it.doctor!!.id == visit.doctor!!.id && it.dateFrom == visit.date }

        val spans = reservations.toTimeSpans()
            .filter { it.fits(visit.services!!.sumOf { it.duration!! }) }

        model["table"] = spans.map { "${it.start} - ${it.end}" }
            .toHtmlTable()

        model["time"] = spans.findBestTime(visit.services!!.sumOf { it.duration!! })

        model["duration"] = visit.services!!.sumOf { it.duration!! }
        return "adminvisits4"
    }

    private fun List<Reservation>.toTimeSpans(): List<TimeSpan> {
        var list: List<String> = listOf()
        var query = QueryBuilder(VISIT)
            .addEquals("date", visit.date.toString())
            .addEquals("doctor.id", visit.doctor!!.id.toString())
            .build()
        val visits = (entityManager!!.createQuery(query).resultList as List<Visit>).sortedBy {
            it.time
        }

        var timeSpans = mutableListOf<TimeSpan>()
        forEach { r ->
            var reservationTimeSpans = listOf(TimeSpan(r.hourFrom!!, r.until!!))
            visits.forEach { v ->
                if (!v.time!!.isBefore(r.hourFrom) && !v.time!!.isAfter(r.until)) {
                    reservationTimeSpans = reservationTimeSpans.takeOut(
                        TimeSpan(
                            v.time!!,
                            v.time!!.plusMinutes(v.services!!.sumOf { it.duration!!.toLong() })
                        )
                    )
                }
            }
            timeSpans.addAll(reservationTimeSpans)
        }
        return timeSpans
    }

    private fun List<TimeSpan>.findBestTime(duration: Int): String {
        filter {
            it.start.plusMinutes(duration.toLong()) == it.end
        }.firstOrNull()?.let{
            return "${it.start}"
        }

        filter {
            it.fits(duration)
        }.firstOrNull()?.let{
            return "${it.start}"
        }

        return "Brak wolnych terminów!"
    }

    private data class TimeSpan(
        val start: LocalTime,
        val end: LocalTime
    )

    private fun List<TimeSpan>.takeOut(span: TimeSpan): List<TimeSpan> {
        val returnSpans = mutableListOf<TimeSpan>()
        forEach {
            if (!span.start.isBefore(it.end) || !span.end.isAfter(it.start))
                returnSpans.add(it)
            else if (span.start == it.start && span.end == it.end)
                Unit
            else if (span.start == it.start && span.end.isBefore(it.end))
                returnSpans.add(
                    TimeSpan(
                        span.end,
                        it.end
                    )
                )
            else if (span.start.isAfter(it.start) && span.end == it.end)
                returnSpans.add(
                    TimeSpan(
                        it.start,
                        span.start
                    )
                )
            else if (span.start.isAfter(it.start) && span.end.isBefore(it.end)) {
                returnSpans.add(
                    TimeSpan(
                        it.start,
                        span.start
                    )
                )
                returnSpans.add(
                    TimeSpan(
                        span.end,
                        it.end
                    )
                )
            }
        }
        return returnSpans
    }

    private fun TimeSpan.fits(v : Visit): Boolean =
        (!v.time!!.isBefore(start) && !v.time!!.plusMinutes(v.services!!.sumOf { it.duration!!.toLong() }).isAfter(end))

    private fun TimeSpan.fits(l : Int): Boolean =
        (!start.plusMinutes(l.toLong()).isAfter(end))

    @RequestMapping(method = [RequestMethod.POST], value = ["/visitsAdminChooseTime"])
    @Transactional
    fun time(@RequestBody data: Visit): String {
        visit.time = data.time
        val reservations = entityManager!!.findAll<Reservation>(RESERVATION)
            .filter { it.doctor!!.id == visit.doctor!!.id && it.dateFrom == visit.date }

        val spans = reservations.toTimeSpans()
            .filter { it.fits(visit.services!!.sumOf { it.duration!! }) }

        if (spans.any { it.fits(visit) }) {
            entityManager.persist(visit)
        } else {
            throw ForbiddenException("Podaj termin w jednym z podanych zakresów czasowych")
        }

        return "good"
    }
}
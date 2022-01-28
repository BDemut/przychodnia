package com.example.demo.controllers

import com.example.demo.Session
import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Doctor
import com.example.demo.model.data.LoginData
import com.example.demo.session
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Controller
class LoginController {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    @GetMapping("/")
    fun login(model: Model): String = "login"

    @RequestMapping(method = [RequestMethod.POST], value = ["/loginPost"])
    @Transactional
    fun loginPost(@RequestBody login: LoginData): String {
        session = Session(login, entityManager!!)
        return "good"
    }

    @GetMapping("/logout")
    fun logout(model: Model): String {
        session = null
        return "logout"
    }

}

package com.example.demo

import com.example.demo.database.entities.Admin
import com.example.demo.database.entities.Doctor
import com.example.demo.model.data.CredentialType
import com.example.demo.model.data.LoginData
import com.example.demo.model.data.User
import javax.persistence.EntityManager

class Session(data: LoginData, entityManager: EntityManager) {
    var user: User? = null

    init {
        val admins: List<Admin> = entityManager
            .createQuery("SELECT a from Admin a where a.pesel = ?1")
            .setParameter(1, data.pesel)
            .resultList as List<Admin>

        val doctors: List<Doctor> = entityManager
            .createQuery("SELECT a from Doctor a where a.pesel = ?1")
            .setParameter(1, data.pesel)
            .resultList as List<Doctor>

        admins.firstOrNull()?.let {
            if (it.password == data.password) {
                user = User(it.pesel!!, it.fname!!, it.lname!!, it.id!!, CredentialType.ADMIN)
            }
        }
        doctors.firstOrNull()?.let {
            if (it.password == data.password) {
                user = User(it.pesel!!, it.fname!!, it.lname!!, it.id!!, CredentialType.DOCTOR)
            }
        }
    }
}
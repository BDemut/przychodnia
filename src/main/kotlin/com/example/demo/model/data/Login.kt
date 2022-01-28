package com.example.demo.model.data

data class User(
    var pesel: String,
    var fname: String,
    var lname: String,
    var id: Int,
    val credentialType: CredentialType
)

data class LoginData (
    val pesel: String,
    val password: String
)

enum class CredentialType {
    ADMIN,
    DOCTOR,
    ERROR
}
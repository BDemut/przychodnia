package com.example.demo.controllers

import com.example.demo.model.data.CredentialType
import com.example.demo.session
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PanelController {
    @GetMapping("/panel")
    fun panel(model: Model): String {
        session?.user?.let {
            return when (it.credentialType) {
                CredentialType.ADMIN -> {
                    model["fname"] = it.fname
                    model["lname"] = it.lname
                    "panelAdmin"
                }
                CredentialType.DOCTOR -> {
                    model["fname"] = it.fname
                    model["lname"] = it.lname
                    "panelDoctor"
                }
                CredentialType.ERROR -> "loginerror"
            }
        }
        return "loginerror"
    }
}
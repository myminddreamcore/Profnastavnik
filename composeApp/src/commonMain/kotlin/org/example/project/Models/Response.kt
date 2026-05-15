package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val idResponse: Int = 0,
    val idCompany: Int? = null,
    val idUser: Int? = null,
    val descriptionResponse: String? = null,
    val portfolioResponse: String? = null,
    val statusResponse: String? = null,
    val idVacancy: Int? = null,
    val dateResponse: String? = null
)
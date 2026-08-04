package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Intership(
    val idIntership: Int = 0,
    val idUser: Int? = null,
    val idCompany: Int? = null,
    val dateStartIntership: String? = null,
    val dateEndIntership: String? = null,
    val statusIntership: String? = null,
    val responseId: Int? = null,
    val idVacancy: Int? = null
)
package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class ResponciesDTO(
    val fIO: String? = null,
    val nameVacancy: String? = null,
    val description: String? = null,
    val idVacancy: Int? = null,
    val idUser: Int? = null
)
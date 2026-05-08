package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Formats(
    val idVacancyFormat: Int = 0,
    val nameVacancyFormat: String,
)
package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class UserVacancies(
    val nameVacancy: String,
    val nameCompany: String? = null,
    val dateEnd: Double? = null
)
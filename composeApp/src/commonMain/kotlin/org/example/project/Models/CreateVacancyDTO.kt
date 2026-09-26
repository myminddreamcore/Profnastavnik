package org.example.project.Models
import org.example.project.Models.Adress
import org.example.project.Models.Vacancy

import kotlinx.serialization.Serializable
@Serializable
data class CreateVacancyDTO(
    val vacancy: Vacancy,
    val adress: Adress,
    val skills: List<String>? = null,
    val sferes: List<String>? = null,
    val format: String? = null
)
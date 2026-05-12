package org.example.project.Models
import kotlinx.serialization.Serializable
@Serializable
data class CardVacancy (
    var vacancy : Vacancy? = null,
    var ratingCompany: Double? = null,
    var nameCompany: String? = null,
    var companyId: Int? = null,
    var formats: List<String>? = null,
    var skills: List<String>? = null,
    var sferes: List<String>? = null,
    var currency: String? = null,
)
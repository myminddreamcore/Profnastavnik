package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class DirectorDTO(
    val director: Director? = null,
    val rating: Double? = null
)
@Serializable
data class PricesForCompany(
    val id: Int = 0,
    val namePrice: String? = null,
    val countVacancy: Int? = null,
    val countResponce: Int? = null,
    val cost: Int? = null,
    val description: String? = null
)

@Serializable
data class CompanyPrice(
    val id: Int = 0,
    val idCompany: Int? = null,
    val idPrice: Int? = null
)

@Serializable
data class StudentSearchFilters(
    val fullName: String? = null,
    val skills: List<String>? = null,
    val professions: List<String>? = null,
    val university: String? = null,
    val minCourse: Int? = null,
    val maxCourse: Int? = null
)

@Serializable
data class TopStudentDTO(
    val idStudent: Int = 0,
    val fullName: String? = null,
    val emailStudent: String? = null,
    val courseStudent: Int? = null,
    val facultatyStudent: String? = null,
    val universityStudent: String? = null,
    val skills: List<String>? = null,
    val professions: List<String>? = null,
    val completedInternships: Int = 0,
    val profileCompleteness: Double = 0.0,
    val vacancyCompatibility: Double = 0.0,
    val studentRating: Double = 0.0,
    val totalScore: Double = 0.0
)
@Serializable
data class PriorityResponseDTO(
    val response: Response,
    val studentFIO: String? = null,
    val vacancyName: String? = null,
    val skillMatchCount: Int = 0,
    val skillMatchPercent: Double = 0.0,
    val professionMatchCount: Int = 0,
    val professionMatchPercent: Double = 0.0,
    val completedInternships: Int = 0,
    val studentRating: Double = 0.0,
    val totalScore: Double = 0.0,
    val hasPortfolio: Boolean = false
)
@Serializable
data class StudentSearchResult(
    val idStudent: Int = 0,
    val fullName: String? = null,
    val emailStudent: String? = null,
    val courseStudent: Int? = null,
    val facultatyStudent: String? = null,
    val universityStudent: String? = null,
    val skills: List<String>? = null,
    val professions: List<String>? = null
)
@Serializable
data class Director(
    val idDirector: Int = 0,
    val emailDirector: String? = null,
    val nameDirector: String? = null,
    val surnameDirector: String? = null,
    val patronymicDirector: String? = null,
    val nameCompanyDirector: String? = null,
    val descriptionDirector: String? = null,
    val websiteDirector: String? = null,
    val phoneDirector: String? = null,
    val statusDirector: String? = null,
    val dateCreatedDirector: String? = null,
    val timeCreatedDirector: String? = null,
    val cityDirector: String? = null,
    val authenticationDirector: Boolean? = null
)
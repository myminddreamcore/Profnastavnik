package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class ComplaintDTO(
    val complaint: Complaint? = null,
    val nameCompany: String? = null,
    val nameVacancy: String? = null
)

@Serializable
data class Complaint(
    val idComplaint: Int = 0,
    val idUser: Int? = null,
    val idCompany: Int? = null,
    val descriptionComplaint: String? = null,
    val idVacancy: Int? = null,
    val statusComplaint: String? = null
)
package org.example.project.Models

import kotlinx.serialization.Serializable


@Serializable
data class RequestsUser(
    val idRequest: Int,
    val idStudent: Int? = null,
    val idFeedback: Int? = null,
    val descriptionRequest: String? = null,
    val statusRequest: String? = null,
    val dateRequest: String? = null,
    val studentName: String? = null,
    val feedbackText: String? = null
)

@Serializable
data class RequestsCompany(
    val idRequest: Int,
    val idCompany: Int? = null,
    val idFeedback: Int? = null,
    val descriptionRequset: String? = null,
    val statusFeedback: String? = null,
    val dateRequest: String? = null,
    val companyName: String? = null,
    val feedbackText: String? = null
)

@Serializable
data class CreateRequestUser(
    val idStudent: Int,
    val idFeedback: Int?,
    val descriptionRequest: String
)

@Serializable
data class CreateRequestCompany(
    val idCompany: Int,
    val idFeedback: Int?,
    val descriptionRequset: String
)
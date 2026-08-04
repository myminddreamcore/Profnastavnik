package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbacksCompany(
    val idFeedbackCompany: Int = 0,
    val idUser: Int? = null,
    val idCompany: Int? = null,
    val descriptionCompany: String? = null,
    val idVacancy: Int? = null,
    val statusFeedbackCompany: String? = null
)
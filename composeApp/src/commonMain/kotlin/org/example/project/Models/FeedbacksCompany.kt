package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbacksCompany(
    val idFeedbackCompany: Int,
    val idCompany: Int? = null,
    val idUser: Int? = null,
    val idVacancy: Int? = null,
    val ratingStudent: Int? = null,
    val descriptionCompany: String? = null,
    val statusFeedbackCompany: String? = null,
    val dateFeedback: String? = null,
    val nameCompany: String? = null
)
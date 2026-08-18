package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbacksCompany(
    val idFeedbackCompany: Int,
    val idCompany: Int,
    val idUser: Int?,
    val idVacancy: Int?,
    val ratingStudent: Int?,
    val descriptionCompany: String?,
    val statusFeedbackCompany: String?
)
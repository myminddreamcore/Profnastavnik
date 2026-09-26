package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbacksUser(
    val idFeedbackUser: Int,
    val idCompany: Int? = null,
    val idUser: Int? = null,
    val idVacancy: Int? = null,
    val ratingFeedbackUser: Int? = null,
    val descriptionFeedbackUser: String? = null,
    val statusFeedbackUser: String? = null,
    val dateFeedback: String? = null,
    val nameStudent: String? = null,
    val nameCompany: String? = null
)
@Serializable
data class FeedbacksUserDTO(
    val f: FeedbacksUser? = null,
    val nameCompany: String? = null,
    val nameVacancy: String? = null
)
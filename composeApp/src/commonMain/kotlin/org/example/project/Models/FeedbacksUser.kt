package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbacksUser(
    val idFeedbackUser: Int = 0,
    val idUser: Int? = null,
    val idCompany: Int? = null,
    val descriptionFeedbackUser: String? = null,
    val ratingFeedbackUser: Int? = null,
    val idVacancy: Int? = null,
    val statusFeedbackUser: String? = null
)
@Serializable
data class FeedbacksUserDTO(
    val f: FeedbacksUser? = null,
    val nameCompany: String? = null,
    val nameVacancy: String? = null
)
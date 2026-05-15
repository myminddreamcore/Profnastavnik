package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class PricesForUser(
    val idPrice: Int = 0,
    val namePrice: String? = null,
    val countResponces: Int? = null,
    val costPrice: Int? = null,
    val descriptionPrice: String? = null
)
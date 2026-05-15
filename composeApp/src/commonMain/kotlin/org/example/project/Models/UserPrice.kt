package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class UserPrice(
    val id: Int = 0,
    val idUser: Int? = null,
    val idPrices: Int? = null
)
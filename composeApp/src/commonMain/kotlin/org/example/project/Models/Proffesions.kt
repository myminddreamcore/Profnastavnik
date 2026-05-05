package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Proffesions(
    val idProfession: Int = 0,
    val nameProfession: String,
)
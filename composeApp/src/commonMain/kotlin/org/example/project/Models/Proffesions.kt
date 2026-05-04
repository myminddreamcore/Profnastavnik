package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Proffesions(
    val idProffesion: Int = 0,
    val nameProffesion: String,
)
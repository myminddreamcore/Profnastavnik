package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Skills(
    val idSkill: Int = 0,
    val nameSkill: String,
)
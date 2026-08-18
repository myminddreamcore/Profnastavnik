package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class AllUserprofile(
    val student: Student? = null,
    val university: String? = "",
    val skills: List<String>? = emptyList(),
    val proffesions: List<String>? = emptyList(),
    val studentFiles: List<String>? = emptyList()
)
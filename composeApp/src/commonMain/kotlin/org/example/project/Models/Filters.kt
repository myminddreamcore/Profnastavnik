package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Filters(
    val skills: List<String>? = null,
    val sferes: List<String>? = null,
    val formats: List<String>? = null,
    val zenstart: Int? = null,
    val zenEnd: Int? = null,
    val iduser: Int? = null,
    val user: Boolean? = null,
    val name: String? = null,
    val city: String? = null
)
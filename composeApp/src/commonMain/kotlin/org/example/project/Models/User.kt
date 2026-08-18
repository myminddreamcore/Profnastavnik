package org.example.project.Models
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val emailUser: String,
    val passwordUser: String? = null,
    val roleUser: String? = null
)

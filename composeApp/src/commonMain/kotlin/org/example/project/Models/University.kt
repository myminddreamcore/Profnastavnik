package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class University(
    val idUniversity: Int = 0,
    val nameUniversity: String,
)
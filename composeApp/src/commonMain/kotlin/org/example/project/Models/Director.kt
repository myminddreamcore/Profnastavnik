package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class DirectorDTO(
    val director: Director? = null,
    val rating: Double? = null
)

@Serializable
data class Director(
    val idDirector: Int = 0,
    val emailDirector: String? = null,
    val nameDirector: String? = null,
    val surnameDirector: String? = null,
    val patronymicDirector: String? = null,
    val nameCompanyDirector: String? = null,
    val descriptionDirector: String? = null,
    val websiteDirector: String? = null,
    val phoneDirector: String? = null,
    val statusDirector: String? = null,
    val dateCreatedDirector: String? = null,
    val timeCreatedDirector: String? = null,
    val cityDirector: String? = null,
    val authenticationDirector: Boolean? = null
)
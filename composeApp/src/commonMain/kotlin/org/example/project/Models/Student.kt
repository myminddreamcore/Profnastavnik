package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val idStudent: Int = 0,
    val emailStudent: String,
    val courseStudent: Int? = null,
    val facultatyStudent: String? = null,
    val universityStudent: Int? = null,

    val birthdayStudent: String? = null,

    val nameStudent: String? = null,
    val surnameStudent: String? = null,
    val patronymicStudent: String? = null,
    val statusStudent: String? = null,

    val dateCreatedStudent: String? = null,
    val timeCreatedStudent: String? = null,

    val photoStudent: String? = null
)
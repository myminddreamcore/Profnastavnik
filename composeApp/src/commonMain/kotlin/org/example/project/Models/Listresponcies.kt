package org.example.project.Models
import kotlinx.serialization.Serializable
@Serializable
data class Listresponcies(
    var idVacancy: Int? = null,
    var nameVacancy: String? = null,
    var idCompany: Int? = null,
    var zenStart: Int? = null,
    var zenEnd: Int? = null,
    var currency: String? = null,
    var time: String? = null
)
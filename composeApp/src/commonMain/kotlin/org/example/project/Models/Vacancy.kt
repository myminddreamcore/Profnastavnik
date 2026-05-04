package org.example.project.Models
import kotlinx.serialization.Serializable
@Serializable
data class Vacancy(
    var idVacancy: Int = 0,
    var idCompany: Int? = null,
    var nameVacancy: String? = null,
    var descriptionVacancy: String? = null,
    var timeVacancy: String? = null,
    var moneyTypeVacancy: Int? = null,
    var anybodyVacancy: Boolean? = null,
    var statusVacancy: String? = null,
    var countUserVacancy: Int? = null,
    var zenStartVacancy: Int? = null,
    var zenEndVacancy: Int? = null,
    var currencyVacancy: Int? = null
)
package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class MoneyType(
    val idMoneyType: Int = 0,
    val nameMoneyType: String? = null
)

@Serializable
data class Currency(
    val idCurrency: Int = 0,
    val nameCurrency: String? = null
)

@Serializable
data class Adress(
    val idAdress: Int = 0,
    val cityAdress: String? = null,
    val streetAdress: String? = null,
    val flatAdress: String? = null,
    val houseAdress: String? = null,
    val indexAdress: Int? = null,
    val idVacancy: Int? = null
)

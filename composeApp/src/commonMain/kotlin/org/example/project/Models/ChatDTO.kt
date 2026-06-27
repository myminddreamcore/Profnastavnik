package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val chat: Chat,
    val nameCompany: String? = null,
    val nameVacancy: String? = null,
    val fioUser: String? = null,
)

@Serializable
data class Chat(
    val idChat: Int,
    val idUser: Int? = null,
    val textChat: String? = null,
    val statusChat: String? = null,
    val sendAtChat: String? = null,
    val idVacancy: Int? = null,
    val idDirector: Int? = null,
    val emailAdmin: String? = null,
    val senderChat: String? = null,
)
package org.example.project.Models

import kotlinx.serialization.Serializable

@Serializable
object CurrentUser {
    var id: Int? = null
    var email: String? = null
    var role: String? = null
    var isAuthorized: Boolean = false

    fun setUser(user: User) {
        this.email = user.emailUser
        this.role = user.roleUser
        this.isAuthorized = true
    }

    fun logout() {
        id = null
        email = null
        role = null
        isAuthorized = false
    }
}
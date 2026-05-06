package org.example.project

import MainScreen
import RegistrationScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.API.ApiClient

@Composable
fun App() {
    var screenState by remember { mutableStateOf(0) }
    val api = remember { ApiClient() }
    var responseType by remember { mutableStateOf("consider") }
    var selectedVacancyId by remember { mutableStateOf(0) }

    val navigationStack = remember { mutableStateListOf(0) }

    fun navigateTo(state: Int) {
        navigationStack.add(state)
    }

    fun goBack() {
        if (navigationStack.size > 1) {
            navigationStack.removeLast()
        }
    }

    val currentState = navigationStack.last()

    val globalNavigate = { target: String ->
        when (target) {
            "Дашборд" -> {
                navigationStack.clear()
                navigationStack.add(3)
            }
            "Поиск" -> {
                navigationStack.clear()
                navigationStack.add(7)
            }
            "Профиль" -> {
                navigationStack.clear()
                navigationStack.add(10)
            }
            "Настройки" -> {
                navigationStack.clear()
                navigationStack.add(8)
            }
        }
    }

    MaterialTheme {
        when (currentState) {
            0 -> MainScreen(
                onNavigateToLogin = {
                    navigateTo(1)
                },
                onNavigateToReg = {
                    navigateTo(2)
                }
            )

            1 -> LoginScreen(
                onSuccess = {
                    navigateTo(3)
                }
            )

            2 -> RegistrationScreen()

            3 -> DashboardStudent(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToResponses = { type ->
                    responseType = type
                    navigateTo(4)
                }
            )

            4 -> ResponsesScreen(
                api = api,
                type = responseType,
                onBack = { goBack() },
                onNavigateToDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                }
            )

            5 -> VacancyDetailScreen(
                api = api,
                onNavigate = globalNavigate,
                onBack = { goBack() },
                vacancyId = selectedVacancyId
            )

            8 -> SettingsScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToFavourites = {
                    responseType = "favourites"
                    navigateTo(4)
                },
                onNavigateToArchive = {
                    responseType = "archive"
                    navigateTo(4)
                },
                onLogout = {
                    navigationStack.clear()
                    navigationStack.add(0)
                }
            )

            7 -> SearchScreen(
                api = api,
                onNavigate = globalNavigate
            )

            10 -> EditProfileScreen(
                api = api,
                onNavigate = globalNavigate,
                onSave = { goBack() }
            )
        }
    }
}
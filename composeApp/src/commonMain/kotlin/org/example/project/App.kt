package org.example.project

import MainScreen
import RegistrationScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.API.ApiClient
import org.example.project.Models.ChatDTO

@Composable
fun App() {
    val api = remember { ApiClient() }
    var responseType by remember { mutableStateOf("consider") }
    var selectedVacancyId by remember { mutableStateOf(0) }
    val navigationStack = remember { mutableStateListOf(0) }
    var selectedChat by remember { mutableStateOf<ChatDTO?>(null) }
    var selectedCompanyId by remember { mutableStateOf(0) }
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
                onNavigateToLogin = { navigateTo(1) },
                onNavigateToReg = { navigateTo(2) }
            )

            1 -> LoginScreen(onSuccess = { navigateTo(3) })
            2 -> RegistrationScreen()

            3 -> DashboardStudent(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToResponses = { type ->
                    responseType = type
                    navigateTo(4)
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onNavigateToChats = { navigateTo(11) }
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

            7 -> SearchScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onNavigateToChats = { navigateTo(11) }
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
                onNavigateToChats = { navigateTo(11) },
                onLogout = {
                    navigationStack.clear()
                    navigationStack.add(0)
                }
            )

            10 -> EditProfileScreen(
                api = api,
                onNavigate = globalNavigate,
                onSave = { goBack() }
            )

            11 -> ChatsScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToChat = { chat ->
                    selectedChat = chat
                    navigateTo(12)
                }
            )
            12 -> {
                if (selectedChat == null) {
                    goBack()
                } else {
                    ChatDetailScreen(
                        api = api,
                        chatDTO = selectedChat!!,
                        onBack = { goBack() },
                        onNavigateToVacancy = { vacancyId ->
                            selectedVacancyId = vacancyId
                            navigateTo(5)
                        },
                        onNavigateToCompany = { companyId ->
                            selectedCompanyId = companyId
                            navigateTo(13)
                        }
                    )
                }
            }
            13 -> CompanyDetailScreen(
                api = api,
                companyId = selectedCompanyId,
                onBack = { goBack() }
            )
        }
    }
}
package org.example.project

import MainScreen
import RegistrationScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.API.ApiClient
import org.example.project.Models.ChatDTO

import org.example.project.UserScreen.AddComplaintScreen
import org.example.project.UserScreen.AddFeedbackScreen
import org.example.project.UserScreen.ChatDetailScreen
import org.example.project.UserScreen.ChatsScreen
import org.example.project.UserScreen.CompanyDetailScreen
import org.example.project.UserScreen.ComplaintDetailScreen
import org.example.project.UserScreen.ComplaintsScreen
import org.example.project.UserScreen.DashboardStudent
import org.example.project.UserScreen.EditProfileScreen
import org.example.project.UserScreen.FeedbackDetailScreen
import org.example.project.UserScreen.FeedbacksScreen
import org.example.project.UserScreen.ResponsesScreen
import org.example.project.UserScreen.SearchScreen
import org.example.project.UserScreen.SettingsScreen
import org.example.project.UserScreen.TariffsScreen
import org.example.project.UserScreen.VacancyDetailScreen

@Composable
fun App() {
    val api = remember { ApiClient() }
    var responseType by remember { mutableStateOf("consider") }
    var selectedVacancyId by remember { mutableStateOf(0) }
    val navigationStack = remember { mutableStateListOf(0) }
    var selectedChat by remember { mutableStateOf<ChatDTO?>(null) }
    var selectedCompanyId by remember { mutableStateOf(0) }
    var selectedFeedbackId by remember { mutableStateOf(0) }
    var selectedComplaintId by remember { mutableStateOf(0) }
    var selectedVacancyForAction by remember { mutableStateOf(0) }
    var selectedCompanyForAction by remember { mutableStateOf<Int?>(null) }
    var showTariffs by remember { mutableStateOf(false) }
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
                onNavigateToTariffs = {
                    navigateTo(20)
                },
                onNavigateToChats = { navigateTo(11) },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },

                )

            4 -> ResponsesScreen(
                api = api,
                type = responseType,
                onBack = { goBack() },
                onNavigateToDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onNavigateToComplaint = { vacancyId, companyId ->
                    selectedVacancyForAction = vacancyId
                    selectedCompanyForAction = companyId
                    navigateTo(18)
                },
                onNavigateToFeedback = { vacancyId, companyId ->
                    selectedVacancyForAction = vacancyId
                    selectedCompanyForAction = companyId
                    navigateTo(19)
                }
            )
            18 -> AddComplaintScreen(
                api = api,
                vacancyId = selectedVacancyForAction,
                companyId = selectedCompanyForAction,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )
            20 -> TariffsScreen(
                api = api,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )

            19 -> AddFeedbackScreen(
                api = api,
                vacancyId = selectedVacancyForAction,
                companyId = selectedCompanyForAction,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )
            5 -> VacancyDetailScreen(
                api = api,
                onNavigate = globalNavigate,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToTariffs = {
                    navigateTo(20)
                },
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
                onNavigateToComplaints = { navigateTo(16) },
                onNavigateToChats = { navigateTo(11) },
                onNavigateToFeedbacks = { navigateTo(13) },
                onNavigateToTariffs = { navigateTo(20) },
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
            15 -> CompanyDetailScreen(
                api = api,
                companyId = selectedCompanyId,
                onBack = { goBack() }
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
                            navigateTo(15)
                        }
                    )
                }
            }
            13 -> FeedbacksScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToFeedbackDetail = { feedbackId ->
                    selectedFeedbackId = feedbackId
                    navigateTo(14)
                }
            )
            14 -> FeedbackDetailScreen(
                api = api,
                feedbackId = selectedFeedbackId,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToVacancy = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                }
            )
            16 -> ComplaintsScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToComplaintDetail = { complaintId ->
                    selectedComplaintId = complaintId
                    navigateTo(17)
                }
            )

            17 -> ComplaintDetailScreen(
                api = api,
                complaintId = selectedComplaintId,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToVacancy = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                }
            )
        }
    }
}
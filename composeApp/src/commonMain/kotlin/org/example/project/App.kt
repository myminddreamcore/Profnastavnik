package org.example.project

import MainScreen
import RegistrationScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.API.ApiClient
import org.example.project.CompanyScreen.CompanyResponsesScreen
import org.example.project.CompanyScreen.CreateVacancyScreen
import org.example.project.CompanyScreen.DashboardCompany
import org.example.project.Models.ChatDTO
import org.example.project.UserScreen.*

@Composable
fun App() {
    val api = remember { ApiClient() }
    var screenState by remember { mutableStateOf(0) }
    var responseType by remember { mutableStateOf("consider") }
    var selectedVacancyId by remember { mutableStateOf(0) }
    var selectedChat by remember { mutableStateOf<ChatDTO?>(null) }
    var selectedCompanyId by remember { mutableStateOf(0) }
    var selectedFeedbackId by remember { mutableStateOf(0) }
    var selectedComplaintId by remember { mutableStateOf(0) }
    var selectedVacancyForAction by remember { mutableStateOf(0) }
    var selectedCompanyForAction by remember { mutableStateOf<Int?>(null) }
    var userRole by remember { mutableStateOf("student") }
    var previousScreen by remember { mutableStateOf(0) }
    var selectedUserId by remember { mutableStateOf(0) }

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
                navigationStack.add(if (userRole == "company") 21 else 3)
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

            1 -> LoginScreen(
                onSuccess = {
                    userRole = "student"
                    navigateTo(3)
                },
                onCompanySuccess = {
                    userRole = "company"
                    navigateTo(21)
                }
            )
            2 -> RegistrationScreen()

            3 -> DashboardStudent(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToResponses = { type ->
                    responseType = type
                    navigateTo(4)
                },
                onNavigateToTariffs = { navigateTo(20) },
                onNavigateToChats = { navigateTo(11) },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                }
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

            5 -> VacancyDetailScreen(
                api = api,
                onNavigate = globalNavigate,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToTariffs = { navigateTo(20) },
                vacancyId = selectedVacancyId,
                isCompany = userRole == "company"
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

            11 -> ChatsScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToChat = { chat ->
                    selectedChat = chat
                    navigateTo(12)
                },
                isCompany = userRole == "company"
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
                        },
                        isCompany = userRole == "company"
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

            15 -> CompanyDetailScreen(
                api = api,
                companyId = selectedCompanyId,
                onBack = { goBack() }
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

            18 -> AddComplaintScreen(
                api = api,
                vacancyId = selectedVacancyForAction,
                companyId = selectedCompanyForAction,
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

            20 -> TariffsScreen(
                api = api,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )

            21 -> DashboardCompany(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToChats = { navigateTo(11) },
                onCreateVacancy = { navigateTo(23) },
                onViewResponses = { navigateTo(24) },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                }
            )

            22 -> UserProfileScreen(
                api = api,
                userId = selectedUserId,
                onBack = { goBack() },
                onViewResume = {
                    println("Посмотреть резюме пользователя $selectedUserId")
                }
            )

            23 -> CreateVacancyScreen(
                api = api,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )

            24 -> CompanyResponsesScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onResponseUpdated = {
                }
            )
        }
    }
}
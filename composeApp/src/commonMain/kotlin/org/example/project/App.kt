package org.example.project

import MainScreen
import RegistrationScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.API.ApiClient
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
    fun navigateTo(state: Int) {
        previousScreen = screenState
        screenState = state
    }

    fun goBack() {
        screenState = previousScreen
    }

    val globalNavigate = { target: String ->
        when (target) {
            "Дашборд" -> {
                previousScreen = screenState
                screenState = if (userRole == "company") 21 else 3
            }
            "Поиск" -> {
                previousScreen = screenState
                screenState = 7
            }
            "Профиль" -> {
                previousScreen = screenState
                screenState = 10
            }
            "Настройки" -> {
                previousScreen = screenState
                screenState = 8
            }
        }
    }

    MaterialTheme {
        when (screenState) {
            0 -> MainScreen(
                onNavigateToLogin = {
                    previousScreen = 0
                    screenState = 1
                },
                onNavigateToReg = {
                    previousScreen = 0
                    screenState = 2
                }
            )

            1 -> LoginScreen(
                onSuccess = {
                    userRole = "student"
                    previousScreen = 1
                    screenState = 3
                },
                onCompanySuccess = {
                    userRole = "company"
                    previousScreen = 1
                    screenState = 21
                }
            )
            2 -> RegistrationScreen()

            3 -> DashboardStudent(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToResponses = { type ->
                    responseType = type
                    previousScreen = 3
                    screenState = 4
                },
                onNavigateToTariffs = {
                    previousScreen = 3
                    screenState = 20
                },
                onNavigateToChats = {
                    previousScreen = 3
                    screenState = 11
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    previousScreen = 3
                    screenState = 5
                }
            )

            4 -> ResponsesScreen(
                api = api,
                type = responseType,
                onBack = { goBack() },
                onNavigateToDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    previousScreen = 4
                    screenState = 5
                },
                onNavigateToComplaint = { vacancyId, companyId ->
                    selectedVacancyForAction = vacancyId
                    selectedCompanyForAction = companyId
                    previousScreen = 4
                    screenState = 18
                },
                onNavigateToFeedback = { vacancyId, companyId ->
                    selectedVacancyForAction = vacancyId
                    selectedCompanyForAction = companyId
                    previousScreen = 4
                    screenState = 19
                }
            )

            5 -> VacancyDetailScreen(
                api = api,
                onNavigate = globalNavigate,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    previousScreen = 5
                    screenState = 15
                },
                onNavigateToTariffs = {
                    previousScreen = 5
                    screenState = 20
                },
                vacancyId = selectedVacancyId,
                isCompany = userRole == "company"
            )

            7 -> SearchScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    previousScreen = 7
                    screenState = 5
                },
                onNavigateToChats = {
                    previousScreen = 7
                    screenState = 11
                }
            )

            8 -> SettingsScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToFavourites = {
                    responseType = "favourites"
                    previousScreen = 8
                    screenState = 4
                },
                onNavigateToArchive = {
                    responseType = "archive"
                    previousScreen = 8
                    screenState = 4
                },
                onNavigateToComplaints = {
                    previousScreen = 8
                    screenState = 16
                },
                onNavigateToChats = {
                    previousScreen = 8
                    screenState = 11
                },
                onNavigateToFeedbacks = {
                    previousScreen = 8
                    screenState = 13
                },
                onNavigateToTariffs = {
                    previousScreen = 8
                    screenState = 20
                },
                onLogout = {
                    screenState = 0
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
                    previousScreen = 11
                    screenState = 12
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
                            previousScreen = 12
                            screenState = 5
                        },
                        onNavigateToCompany = { companyId ->
                            selectedCompanyId = companyId
                            previousScreen = 12
                            screenState = 15
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
                    previousScreen = 13
                    screenState = 14
                }
            )

            14 -> FeedbackDetailScreen(
                api = api,
                feedbackId = selectedFeedbackId,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    previousScreen = 14
                    screenState = 15
                },
                onNavigateToVacancy = { vacancyId ->
                    selectedVacancyId = vacancyId
                    previousScreen = 14
                    screenState = 5
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
                    previousScreen = 16
                    screenState = 17
                }
            )

            17 -> ComplaintDetailScreen(
                api = api,
                complaintId = selectedComplaintId,
                onBack = { goBack() },
                onNavigateToCompany = { companyId ->
                    selectedCompanyId = companyId
                    previousScreen = 17
                    screenState = 15
                },
                onNavigateToVacancy = { vacancyId ->
                    selectedVacancyId = vacancyId
                    previousScreen = 17
                    screenState = 5
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
                onNavigateToChats = {
                    previousScreen = 21
                    screenState = 11
                },
                onCreateVacancy = {
                    navigateTo(23)
                },
                onViewResponses = {
                    println("Посмотреть отклики")
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    previousScreen = 21
                    screenState = 5
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
        }
    }
}
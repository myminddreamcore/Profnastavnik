package org.example.project

import MainScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.API.ApiClient
import org.example.project.Admin.AdminRequestsScreen
import org.example.project.Admin.AdminSearchScreen
import org.example.project.Admin.DashboardAdmin
import org.example.project.Admin.ModerationScreen
import org.example.project.CompanyScreen.*
import org.example.project.Models.Chat
import org.example.project.Models.ChatDTO
import org.example.project.Models.CurrentUser
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
    var chatWithStudent by remember { mutableStateOf<ChatDTO?>(null) }
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
                navigationStack.add(
                    when (userRole) {
                        "company" -> 21
                        "admin" -> 32
                        else -> 3
                    }
                )
            }
            "Поиск" -> {
                navigationStack.clear()
                navigationStack.add(
                    when (userRole) {
                        "company" -> 30
                        "admin" -> 33
                        else -> 7
                    }
                )
            }
            "Профиль" -> {
                navigationStack.clear()
                navigationStack.add(
                    when (userRole) {
                        "company" -> 26
                        "admin" -> 34
                        else -> 10
                    }
                )
            }
            "Модерация" -> {
                navigationStack.clear()
                navigationStack.add(34)
            }
            "Жалобы" -> {
                navigationStack.clear()
                navigationStack.add(38)
            }
            "Настройки" -> {
                navigationStack.clear()
                navigationStack.add(
                    when (userRole) {
                        "company" -> 25
                        "admin" -> 35
                        else -> 8
                    }
                )
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
                },
                onAdminSuccess = {
                    userRole = "admin"
                    navigateTo(32)
                },
                onNavigateToRegistration = {
                    navigateTo(0)
                }
            )

            2 -> RegistrationScreen(
                onSuccess = { navigateTo(1) },
                onNavigateBack = { navigateTo(0) }
            )

            // ========== СТУДЕНТ ==========
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
                isCompany = userRole == "company" || userRole == "admin"
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
                onNavigateToComplaints = { navigateTo(35) },
                onNavigateToChats = { navigateTo(11) },
                onNavigateToFeedbacks = { navigateTo(13) },
                onNavigateToTariffs = { navigateTo(20) },
                onLogout = {
                    userRole = "student"
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
                isCompany = userRole == "company" || userRole == "admin"
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
                        onNavigateToUser = { userId ->
                            selectedUserId = userId
                            navigateTo(22)
                        },
                        isCompany = userRole == "company" || userRole == "admin"
                    )
                }
            }

            13 -> FeedbacksScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToCompanyProfile = { it ->
                    selectedCompanyId = it
                    navigateTo(15)
                },
                onNavigateToVacancyDetail = { it ->
                    selectedVacancyId = it
                    navigateTo(5)
                },
                onNavigateToCreateRequest = { feedbackId, feedbackText ->
                    selectedFeedbackId = feedbackId
                    navigateTo(36)
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
                },
                onNavigateToChat = { userId ->
                    selectedUserId = userId
                    val chat = Chat(
                        idChat = 0,
                        idUser = userId,
                        textChat = "",
                        statusChat = "",
                        sendAtChat = null,
                        idVacancy = null,
                        idDirector = CurrentUser.id ?: 0,
                        emailAdmin = null,
                        senderChat = ""
                    )
                    selectedChat = ChatDTO(
                        chat = chat,
                        nameCompany = null,
                        nameVacancy = null,
                        fioUser = null
                    )
                    navigateTo(31)
                }
            )

            23 -> CreateVacancyScreen(
                api = api,
                onBack = { goBack() },
                onSuccess = { goBack() },
                onNavigateToTariffs = { navigateTo(29) }
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
                onResponseUpdated = { }
            )

            25 -> CompanySettingsScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToChats = { navigateTo(11) },
                onNavigateToTariffs = { navigateTo(29) },
                onNavigateToArchive = {
                    responseType = "archive"
                    navigateTo(4)
                },
                onNavigateToFeedbacks = { navigateTo(27) },
                onNavigateToArchiveShip = { navigateTo(28) },
                onNavigateToComplaint= { navigateTo(35) },
                onLogout = {
                    userRole = "student"
                    navigationStack.clear()
                    navigationStack.add(0)
                }
            )

            26 -> EditCompanyProfileScreen(
                api = api,
                onNavigate = globalNavigate,
                onSave = { goBack() }
            )

            27 -> CompanyFeedbacksScreen(
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
                onNavigateToCreateRequest = { feedbackId, feedbackText ->
                    selectedFeedbackId = feedbackId
                    navigateTo(36)
                }
            )

            28 -> CompanyArchiveScreen(
                api = api,
                onBack = { goBack() },
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                }
            )

            29 -> CompanyTariffsScreen(
                api = api,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )

            30 -> CompanySearchScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                },
                onNavigateToTariffs = { navigateTo(29) }
            )

            31 -> {
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
                        onNavigateToUser = { userId ->
                            selectedUserId = userId
                            navigateTo(22)
                        },
                        isCompany = userRole == "company" || userRole == "admin"
                    )
                }
            }

            // ========== АДМИН ==========
            32 -> DashboardAdmin(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToStudentProfile = { studentId ->
                    selectedUserId = studentId
                    navigateTo(22)
                },
                onNavigateToCompanyProfile = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToChats = {navigateTo(11)
                },
                onLogout = {navigateTo(0)
                },
                onToggleTheme = {navigateTo(0)
                },
            )

            33 -> AdminSearchScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                },
                onNavigateToCompanyProfile = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onNavigateToFeedbackDetail = { feedbackId ->
                    selectedFeedbackId = feedbackId
                    navigateTo(14)
                },
                onNavigateToChat = { id, type ->
                    val chat = Chat(
                        idChat = 0,
                        idUser = if (type == "user") id else null,
                        textChat = "",
                        statusChat = "",
                        sendAtChat = null,
                        idVacancy = null,
                        idDirector = if (type == "company") id else null,
                        emailAdmin = CurrentUser.email,
                        senderChat = ""
                    )
                    selectedChat = ChatDTO(
                        chat = chat,
                        nameCompany = null,
                        nameVacancy = null,
                        fioUser = null
                    )
                    navigateTo(31)
                }
            )
            34 -> ModerationScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                },
                onNavigateToCompanyProfile = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToVacancyDetail = { vacancyId ->
                    selectedVacancyId = vacancyId
                    navigateTo(5)
                },
                onNavigateToFeedbackDetail = { feedbackId ->
                    selectedFeedbackId = feedbackId
                    navigateTo(14)
                }
            )




            35 -> MyRequestsScreen(
                api = api,
                onBack = { goBack() },
                userRole = userRole,
                onNavigateToCreateDirectRequest = {
                    selectedUserId = 0
                    selectedCompanyId = 0
                    navigateTo(37)
                }
            )

            36 -> CreateRequestScreen(
                api = api,
                feedbackId = selectedFeedbackId,
                feedbackText = null,
                onBack = { goBack() },
                onSuccess = { goBack() },
                userRole = userRole
            )


            37 -> CreateDirectRequestScreen(
                api = api,
                targetType = if (selectedUserId > 0) "user" else "company",
                targetId = if (selectedUserId > 0) selectedUserId else selectedCompanyId,
                targetName = null,
                userRole = userRole,
                onBack = { goBack() },
                onSuccess = { goBack() }
            )
            38 -> AdminRequestsScreen(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToUserProfile = { userId ->
                    selectedUserId = userId
                    navigateTo(22)
                },
                onNavigateToCompanyProfile = { companyId ->
                    selectedCompanyId = companyId
                    navigateTo(15)
                },
                onNavigateToFeedbackDetail = { feedbackId ->
                    selectedFeedbackId = feedbackId
                    navigateTo(14)
                }
            )

        }
    }
}
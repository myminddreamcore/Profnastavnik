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

    val globalNavigate = { target: String ->
        screenState = when (target) {
            "Дашборд" -> 3
            "Поиск" -> 7
            "Профиль" -> 10
            "Настройки" -> 8
            else -> 3
        }
    }

    MaterialTheme {
        when (screenState) {
            0 -> MainScreen(
                onNavigateToLogin = { screenState = 1 },
                onNavigateToReg = { screenState = 2 }
            )
            1 -> LoginScreen(onSuccess = { screenState = 3 })
            2 -> RegistrationScreen()

            3 -> DashboardStudent(
                api = api,
                onNavigate = globalNavigate,
                onNavigateToResponses = { type ->
                    responseType = type
                    screenState = 4
                }
            )

            4 -> ResponsesScreen(
                api = api,
                type = responseType,
                onBack = { screenState = 3 },
                onNavigateToDetail = { screenState = 5 }
            )

            5 -> VacancyDetailScreen(
                api = api,
                onNavigate = globalNavigate,
                onBack = { screenState = 3 }
            )

            10 -> EditProfileScreen(
                api = api,
                onNavigate = globalNavigate,
                onSave = { screenState = 3 }
            )

//            7 -> Box(Modifier.fillMaxSize()) { Text("Экран Поиска") }
//            8 -> Box(Modifier.fillMaxSize()) { Text("Экран Настроек") }
        }
    }
}
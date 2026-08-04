package org.example.project.CompanyScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CurrentUser
import org.example.project.Models.FeedbacksCompany
import org.example.project.Models.Intership
import org.example.project.Models.Student
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun CompanyArchiveScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit
) {
    var internships by remember { mutableStateOf<List<Intership>>(emptyList()) }
    var userNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var selectedInternship by remember { mutableStateOf<Intership?>(null) }
    var feedbackText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadData() {
        scope.launch {
            isLoading = true
            val companyId = CurrentUser.id ?: 0

            val result = api.getCompanyInterships(companyId)
            if (result != null) {
                internships = result

                // Загружаем имена пользователей
                val namesMap = mutableMapOf<Int, String>()
                result.forEach { internship ->
                    internship.idUser?.let { userId ->
                        if (!namesMap.containsKey(userId)) {
                            val student = api.getUserById(userId)
                            if (student != null) {
                                val fullName = "${student.surnameStudent ?: ""} ${student.nameStudent ?: ""} ${student.patronymicStudent ?: ""}".trim()
                                namesMap[userId] = fullName.ifEmpty { "Пользователь" }
                            } else {
                                namesMap[userId] = "Пользователь"
                            }
                        }
                    }
                }
                userNames = namesMap
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun sendFeedback() {
        if (feedbackText.isBlank()) {
            errorMessage = "Введите текст отзыва"
            return
        }

        scope.launch {
            isSending = true
            errorMessage = null

            val feedback = FeedbacksCompany(
                idFeedbackCompany = 0,
                idUser = selectedInternship?.idUser,
                idCompany = CurrentUser.id ?: 0,
                descriptionCompany = feedbackText,
                idVacancy = selectedInternship?.idVacancy,
                statusFeedbackCompany = "Новый"
            )

            val success = api.addCompanyFeedback(feedback)
            isSending = false

            if (success) {
                snackbarHostState.showSnackbar("Отзыв успешно отправлен!")
                showFeedbackDialog = false
                feedbackText = ""
                selectedInternship = null
                loadData()
            } else {
                errorMessage = "Вы уже оставляли отзыв на эту стажировку"
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Text("Архив стажировок", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (internships.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Archive, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Нет завершенных стажировок", color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(internships) { internship ->
                            val userName = internship.idUser?.let { userNames[it] } ?: "Пользователь"
                            ArchiveCard(
                                internship = internship,
                                userName = userName,
                                onViewUser = { internship.idUser?.let { onNavigateToUserProfile(it) } },
                                onViewVacancy = { internship.idVacancy?.let { onNavigateToVacancyDetail(it) } },
                                onAddFeedback = {
                                    selectedInternship = internship
                                    showFeedbackDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Диалог добавления отзыва
    if (showFeedbackDialog && selectedInternship != null) {
        AlertDialog(
            onDismissRequest = {
                showFeedbackDialog = false
                feedbackText = ""
                errorMessage = null
            },
            title = {
                Text(
                    "Добавить отзыв",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Оставьте отзыв о стажере",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = {
                            feedbackText = it
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        placeholder = {
                            Text(
                                "Напишите отзыв о стажере...",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF5399BC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMessage!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = { sendFeedback() },
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC))
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Отправка...", color = Color.White)
                    } else {
                        Text("Отправить", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showFeedbackDialog = false
                        feedbackText = ""
                        errorMessage = null
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Отмена", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ArchiveCard(
    internship: Intership,
    userName: String,
    onViewUser: () -> Unit,
    onViewVacancy: () -> Unit,
    onAddFeedback: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Имя пользователя - кликабельно
            Text(
                userName,
                color = Color(0xFF5399BC),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onViewUser() }
            )

            Spacer(modifier = Modifier.height(4.dp))



            Spacer(modifier = Modifier.height(8.dp))

            // Даты
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Начало: ${internship.dateStartIntership ?: "Не указано"}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Text(
                    "Окончание: ${internship.dateEndIntership ?: "Не указано"}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Статус
            if (!internship.statusIntership.isNullOrBlank()) {
                Surface(
                    color = when (internship.statusIntership) {
                        "Закончена" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "Активна" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        internship.statusIntership,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddFeedback,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.RateReview, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Добавить отзыв", color = Color.White, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onViewVacancy,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5399BC))
                ) {
                    Icon(Icons.Default.Work, null, tint = Color(0xFF5399BC), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Вакансия", color = Color(0xFF5399BC), fontSize = 12.sp)
                }
            }
        }
    }
}
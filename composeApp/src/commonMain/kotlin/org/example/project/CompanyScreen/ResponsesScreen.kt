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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CurrentUser
import org.example.project.Models.PriorityResponseDTO
import org.example.project.Models.CompanyPrice
import org.example.project.Models.Vacancy

@Composable
fun CompanyResponsesScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onResponseUpdated: () -> Unit
) {
    var priorityResponses by remember { mutableStateOf<List<PriorityResponseDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedResponse by remember { mutableStateOf<PriorityResponseDTO?>(null) }
    var actionType by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var companyPrice by remember { mutableStateOf<CompanyPrice?>(null) }
    var isLoadingTariff by remember { mutableStateOf(true) }
    var showPriorityInfo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadData() {
        scope.launch {
            isLoading = true
            isLoadingTariff = true

            val companyId = CurrentUser.id ?: 0

            // Загружаем тариф
            val tariff = api.getCompanyTariff(companyId)
            companyPrice = tariff

            // Загружаем приоритетные отклики
            val result = api.getPriorityResponses(companyId)
            if (result != null) {
                priorityResponses = result
            }

            isLoading = false
            isLoadingTariff = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            snackbarHostState.showSnackbar(snackbarMessage!!)
            delay(1500)
            if (isSuccess) {
                onBack()
            }
        }
    }

    fun updateResponse(responseId: Int, status: String, actionName: String) {
        scope.launch {
            val success = api.updateResponseStatus(responseId, status)
            if (success) {
                showConfirmDialog = false
                snackbarMessage = "Стажер $actionName успешно!"
                isSuccess = true
                onResponseUpdated()
                loadData()
            } else {
                snackbarMessage = "Ошибка при обновлении статуса"
                isSuccess = false
            }
        }
    }

    // Проверяем, доступны ли приоритетные отклики (тариф 2 или 3)
    val isPriorityAvailable = companyPrice?.idPrice in listOf(2, 3)

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
                    Text(
                        if (isPriorityAvailable) "Приоритетные отклики" else "Новые отклики",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Кнопка информации о приоритетах
                    if (isPriorityAvailable) {
                        IconButton(onClick = { showPriorityInfo = !showPriorityInfo }) {
                            Icon(
                                Icons.Default.Info,
                                null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Информация о приоритетных откликах
                if (showPriorityInfo && isPriorityAvailable) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB74D).copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Рейтинг приоритетности рассчитывается по:",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "• Совпадение навыков (25%)\n• Совпадение профессий (20%)\n• Количество завершенных стажировок (20%)\n• Рейтинг стажера (25%)\n• Наличие портфолио (+10%)",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (priorityResponses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Assignment, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                if (isPriorityAvailable) "Нет приоритетных откликов" else "Новых откликов нет",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(priorityResponses) { priorityResponse ->
                            PriorityResponseCard(
                                priorityResponse = priorityResponse,
                                isPriorityAvailable = isPriorityAvailable,
                                onAccept = {
                                    selectedResponse = priorityResponse
                                    actionType = "accept"
                                    showConfirmDialog = true
                                },
                                onReject = {
                                    selectedResponse = priorityResponse
                                    actionType = "reject"
                                    showConfirmDialog = true
                                },
                                onViewProfile = {
                                    priorityResponse.response.idUser?.let { onNavigateToUserProfile(it) }
                                },
                                onViewVacancy = {
                                    priorityResponse.response.idVacancy?.let { onNavigateToVacancyDetail(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog && selectedResponse != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    if (actionType == "accept") "Пригласить стажера" else "Отклонить отклик",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        if (actionType == "accept") "Вы уверены, что хотите пригласить этого стажера?"
                        else "Вы уверены, что хотите отклонить этот отклик?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = {
                        selectedResponse?.response?.idResponse?.let {
                            updateResponse(
                                it,
                                if (actionType == "accept") "Приглашен" else "Отклонено",
                                if (actionType == "accept") "приглашен" else "отклонен"
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (actionType == "accept") Color(0xFF4CAF50) else Color(0xFFB71C1C)
                    )
                ) {
                    Text(if (actionType == "accept") "Пригласить" else "Отклонить", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmDialog = false }) {
                    Text("Отмена", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun PriorityResponseCard(
    priorityResponse: PriorityResponseDTO,
    isPriorityAvailable: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onViewProfile: () -> Unit,
    onViewVacancy: () -> Unit
) {
    val response = priorityResponse.response

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Рейтинг и ФИО
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    priorityResponse.studentFIO ?: "Студент",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )


            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Вакансия: ${priorityResponse.vacancyName ?: "Не указана"}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            if (isPriorityAvailable) {
                Surface(
                    color = Color(0xFFFFB74D).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Whatshot,
                            null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${priorityResponse.totalScore}%",
                            color = Color(0xFFFFB74D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Статистика совпадений (только для приоритетных)
            if (isPriorityAvailable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(
                        label = "Навыки",
                        value = "${priorityResponse.skillMatchCount} (${priorityResponse.skillMatchPercent}%)",
                        color = Color(0xFF4A90E2)
                    )
                    StatChip(
                        label = "Профессии",
                        value = "${priorityResponse.professionMatchCount} (${priorityResponse.professionMatchPercent}%)",
                        color = Color(0xFF7B61FF)
                    )
                    StatChip(
                        label = "Стажировки",
                        value = "${priorityResponse.completedInternships}",
                        color = Color(0xFF00E676)
                    )
                }

                if (priorityResponse.hasPortfolio) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Есть портфолио", color = Color(0xFF4CAF50), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Сопроводительное письмо
            Text(
                "Сопроводительное письмо:",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Text(
                    response.descriptionResponse ?: "Нет сопроводительного письма",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            if (!response.portfolioResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, null, tint = Color(0xFF5399BC), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Портфолио: ${response.portfolioResponse}", color = Color(0xFF5399BC), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradientActionButton(
                    text = "Пригласить",
                    icon = Icons.Default.Check,
                    onClick = onAccept,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFF00E676)),
                    modifier = Modifier.weight(1f)
                )
                GradientActionButton(
                    text = "Отклонить",
                    icon = Icons.Default.Close,
                    onClick = onReject,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFFB71C1C)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradientActionButton(
                    text = "Профиль",
                    icon = Icons.Default.Person,
                    onClick = onViewProfile,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF)),
                    modifier = Modifier.weight(1f)
                )
                GradientActionButton(
                    text = "Вакансия",
                    icon = Icons.Default.Work,
                    onClick = onViewVacancy,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF)),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
            Text(
                value,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun GradientActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.horizontalGradient(gradient),
                shape = RoundedCornerShape(12.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
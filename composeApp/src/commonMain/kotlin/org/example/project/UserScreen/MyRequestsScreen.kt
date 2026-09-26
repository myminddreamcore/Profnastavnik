package org.example.project.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.example.project.Models.RequestsCompany
import org.example.project.Models.RequestsUser

@Composable
fun MyRequestsScreen(
    api: ApiClient,
    onBack: () -> Unit,
    userRole: String,
    onNavigateToCreateDirectRequest: () -> Unit  // <-- НОВЫЙ КОЛБЭК
) {
    var userRequests by remember { mutableStateOf<List<RequestsUser>>(emptyList()) }
    var companyRequests by remember { mutableStateOf<List<RequestsCompany>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun loadRequests() {
        scope.launch {
            isLoading = true

            if (userRole == "student") {
                val userId = CurrentUser.id ?: 0
                val result = api.getUserRequests(userId)
                if (result != null) userRequests = result
            } else if (userRole == "company") {
                val companyId = CurrentUser.id ?: 0
                val result = api.getCompanyRequests(companyId)
                if (result != null) companyRequests = result
            }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadRequests()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Верхняя панель
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    "Мои жалобы",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Кнопка создания жалобы
                IconButton(
                    onClick = onNavigateToCreateDirectRequest
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Создать жалобу",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                val items = if (userRole == "student") userRequests else companyRequests

                if (items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ReportOff,
                                null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "У вас нет отправленных жалоб",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = onNavigateToCreateDirectRequest
                            ) {
                                Text(
                                    "Создать жалобу",
                                    color = Color(0xFF5399BC)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items) { item ->
                            if (userRole == "student") {
                                RequestUserCard(item as RequestsUser)
                            } else {
                                RequestCompanyCard(item as RequestsCompany)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RequestUserCard(request: RequestsUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Жалоба на отзыв",
                    color = Color(0xFFFFB74D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = when (request.statusRequest) {
                        "На рассмотрении" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        "Одобрено" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "Отклонено" -> Color(0xFFB71C1C).copy(alpha = 0.2f)
                        else -> Color.White.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        request.statusRequest ?: "Неизвестно",
                        color = when (request.statusRequest) {
                            "На рассмотрении" -> Color(0xFFFF9800)
                            "Одобрено" -> Color(0xFF4CAF50)
                            "Отклонено" -> Color(0xFFB71C1C)
                            else -> Color.White.copy(alpha = 0.7f)
                        },
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Отзыв: ${request.feedbackText ?: "Нет текста"}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Причина жалобы:",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Text(
                request.descriptionRequest ?: "Нет описания",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!request.dateRequest.isNullOrBlank()) {
                Text(
                    "Дата: ${request.dateRequest}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun RequestCompanyCard(request: RequestsCompany) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Жалоба на отзыв",
                    color = Color(0xFFFFB74D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = when (request.statusFeedback) {
                        "На рассмотрении" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        "Одобрено" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "Отклонено" -> Color(0xFFB71C1C).copy(alpha = 0.2f)
                        else -> Color.White.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        request.statusFeedback ?: "Неизвестно",
                        color = when (request.statusFeedback) {
                            "На рассмотрении" -> Color(0xFFFF9800)
                            "Одобрено" -> Color(0xFF4CAF50)
                            "Отклонено" -> Color(0xFFB71C1C)
                            else -> Color.White.copy(alpha = 0.7f)
                        },
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Отзыв: ${request.feedbackText ?: "Нет текста"}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Причина жалобы:",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Text(
                request.descriptionRequset ?: "Нет описания",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!request.dateRequest.isNullOrBlank()) {
                Text(
                    "Дата: ${request.dateRequest}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
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
import org.example.project.Models.FeedbacksUserDTO

@Composable
fun FeedbacksScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onNavigateToFeedbackDetail: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var feedbacks by remember { mutableStateOf<List<FeedbacksUserDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFeedbackId by remember { mutableStateOf<Int?>(null) }

    fun loadFeedbacks() {
        scope.launch {
            isLoading = true
            val userId = CurrentUser.id ?: 0
            val result = api.getUserFeedbacks(userId)
            if (result != null) {
                feedbacks = result.filter { it.f?.statusFeedbackUser != "Удален" }
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadFeedbacks()
    }

    fun deleteFeedback() {
        scope.launch {
            selectedFeedbackId?.let { id ->
                val success = api.deleteFeedback(id)
                if (success) {
                    feedbacks = emptyList()
                    loadFeedbacks()
                } else {
                    println("Ошибка при удалении")
                }
            }
            showDeleteDialog = false
            selectedFeedbackId = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
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
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    "Мои отзывы",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                feedbacks.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Report, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("У вас пока нет отзывов", color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(feedbacks) { item ->
                            FeedbackCard(
                                item = item,
                                onClick = { onNavigateToFeedbackDetail(item.f?.idFeedbackUser ?: 0) },
                                onDelete = {
                                    selectedFeedbackId = item.f?.idFeedbackUser
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Удаление отзыва",
                    color = Color(0xFFB71C1C),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Вы уверены, что хотите удалить этот отзыв?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Это действие нельзя отменить.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { deleteFeedback() }
                ) {
                    Text("Удалить", color = Color(0xFFB71C1C))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF1E1E2E),
            titleContentColor = Color(0xFFB71C1C),
            textContentColor = Color.White
        )
    }
}

@Composable
fun FeedbackCard(
    item: FeedbacksUserDTO,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.nameCompany ?: "Компания",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        item.nameVacancy ?: "Вакансия",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color(0xFFB71C1C),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Оценка: ", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(
                        "${item.f?.ratingFeedbackUser ?: "Нет"}",
                        color = Color(0xFFFFB74D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    repeat(item.f?.ratingFeedbackUser ?: 0) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB74D), modifier = Modifier.size(14.dp))
                    }
                }

                Surface(
                    color = when (item.f?.statusFeedbackUser) {
                        "Новая" -> Color(0xFFFF9800)
                        "Рассмотрена" -> Color(0xFF4CAF50)
                        else -> Color(0xFF9E9E9E)
                    }.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        item.f?.statusFeedbackUser ?: "Неизвестно",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                item.f?.descriptionFeedbackUser ?: "Нет описания",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onClick() }
            )
        }
    }
}
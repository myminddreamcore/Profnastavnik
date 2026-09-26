package org.example.project.UserScreen

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
import org.example.project.Models.FeedbacksUserDTO

@Composable
fun FeedbacksScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onNavigateToCompanyProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToCreateRequest: (Int, String?) -> Unit
) {
    var reviewsFromCompanies by remember { mutableStateOf<List<FeedbacksCompany>>(emptyList()) }
    var reviewsToCompanies by remember { mutableStateOf<List<FeedbacksUserDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFeedbackId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isLoading = true
            val userId = CurrentUser.id ?: 0

            val fromCompanies = api.getUserFeedbacksCompany(userId)
            if (fromCompanies != null) {
                reviewsFromCompanies = fromCompanies
            }

            val toCompanies = api.getUserFeedbacks(userId)
            if (toCompanies != null) {
                reviewsToCompanies = toCompanies.filter { it.f?.statusFeedbackUser != "Удален" }
            }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun deleteFeedback(feedbackId: Int) {
        scope.launch {
            val success = api.deleteFeedback(feedbackId)
            if (success) {
                showDeleteDialog = false
                selectedFeedbackId = null
                loadData()
            }
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
                    Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Text("Мои отзывы", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Отзывы о пользователе (от компаний)
                    item {
                        Text(
                            "Отзывы о вас (${reviewsFromCompanies.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    if (reviewsFromCompanies.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Нет отзывов о вас", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(reviewsFromCompanies) { review ->
                            ReviewFromCompanyCard(
                                review = review,
                                onViewCompany = { review.idCompany?.let { onNavigateToCompanyProfile(it) } },
                                onViewVacancy = { review.idVacancy?.let { onNavigateToVacancyDetail(it) } },
                                onComplain = {
                                    onNavigateToCreateRequest(
                                        review.idFeedbackCompany ?: 0,
                                        review.descriptionCompany
                                    )
                                }
                            )
                        }
                    }

                    // Отзывы от пользователя (о компаниях)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Ваши отзывы о компаниях (${reviewsToCompanies.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    if (reviewsToCompanies.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Вы еще не оставляли отзывы", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(reviewsToCompanies) { review ->
                            ReviewToCompanyCard(
                                review = review,
                                onViewCompany = { review.f?.idCompany?.let { onNavigateToCompanyProfile(it) } },
                                onViewVacancy = { review.f?.idVacancy?.let { onNavigateToVacancyDetail(it) } },
                                onDelete = {
                                    selectedFeedbackId = review.f?.idFeedbackUser
                                    showDeleteDialog = true
                                },
                                onComplain = {
                                    onNavigateToCreateRequest(
                                        review.f?.idFeedbackUser ?: 0,
                                        review.f?.descriptionFeedbackUser
                                    )
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
                    "Удалить отзыв",
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
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = {
                        selectedFeedbackId?.let { deleteFeedback(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                ) {
                    Text("Удалить", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedFeedbackId = null
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
fun ReviewFromCompanyCard(
    review: FeedbacksCompany,
    onViewCompany: () -> Unit,
    onViewVacancy: () -> Unit,  // <-- ДОБАВЛЕНО
    onComplain: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        review.nameCompany ?: "Компания",
                        color = Color(0xFF5399BC),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onViewCompany() }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            if (index < (review.ratingStudent ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                            null,
                            tint = if (index < (review.ratingStudent ?: 0)) Color(0xFFFFB74D) else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${review.ratingStudent}/5",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        review.dateFeedback ?: "",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )

                    IconButton(
                        onClick = onComplain,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Report,
                            contentDescription = "Пожаловаться",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                review.descriptionCompany ?: "Нет описания",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (!review.statusFeedbackCompany.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (review.statusFeedbackCompany) {
                        "Новый" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        "Опубликован" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        review.statusFeedbackCompany,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Кнопка "Перейти к вакансии"
            Button(
                onClick = onViewVacancy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC).copy(alpha = 0.8f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Work, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Перейти к вакансии", color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ReviewToCompanyCard(
    review: FeedbacksUserDTO,
    onViewCompany: () -> Unit,
    onViewVacancy: () -> Unit,
    onDelete: () -> Unit,
    onComplain: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    review.nameCompany ?: "Компания",
                    color = Color(0xFF5399BC),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewCompany() }
                )

                Row {
                    IconButton(
                        onClick = onComplain,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Report,
                            contentDescription = "Пожаловаться",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = Color(0xFFB71C1C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                review.nameVacancy ?: "Вакансия",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Оценка: ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                repeat(5) { index ->
                    Icon(
                        if (index < (review.f?.ratingFeedbackUser ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                        null,
                        tint = if (index < (review.f?.ratingFeedbackUser ?: 0)) Color(0xFFFFB74D) else Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${review.f?.ratingFeedbackUser}/5",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    review.f?.dateFeedback ?: "",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                review.f?.descriptionFeedbackUser ?: "Нет описания",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewVacancy,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Work, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Вакансия", color = Color.White, fontSize = 11.sp)
                }
            }

            if (!review.f?.statusFeedbackUser.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (review.f?.statusFeedbackUser) {
                        "Новая" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        "Рассмотрена" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        review.f?.statusFeedbackUser ?: "",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
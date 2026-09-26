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
import org.example.project.Models.FeedbacksUser
import org.example.project.Models.Student
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun CompanyFeedbacksScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToCreateRequest: (Int, String?) -> Unit
) {
    var reviewsFromUsers by remember { mutableStateOf<List<FeedbacksUser>>(emptyList()) }
    var reviewsToUsers by remember { mutableStateOf<List<FeedbacksCompany>>(emptyList()) }
    var userNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFeedbackId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isLoading = true
            val companyId = CurrentUser.id ?: 0

            val fromUsers = api.getCompanyFeedback(companyId)
            if (fromUsers != null) {
                reviewsFromUsers = fromUsers
            }

            val toUsers = api.getCompanyFeedbackToUser(companyId)
            if (toUsers != null) {
                reviewsToUsers = toUsers

                val namesMap = mutableMapOf<Int, String>()
                toUsers.forEach { review ->
                    review.idUser?.let { userId ->
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

    fun deleteFeedback(feedbackId: Int) {
        scope.launch {
            val success = api.deleteCompanyFeedback(feedbackId)
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
                    // Отзывы о компании
                    item {
                        Text(
                            "Отзывы о компании (${reviewsFromUsers.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    if (reviewsFromUsers.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Нет отзывов о компании", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(reviewsFromUsers) { review ->
                            ReviewFromUserCard(
                                review = review,
                                onViewProfile = { review.idUser?.let { onNavigateToUserProfile(it) } },
                                onViewVacancy = { review.idVacancy?.let { onNavigateToVacancyDetail(it) } },
                                onComplain = {
                                    onNavigateToCreateRequest(
                                        review.idFeedbackUser ?: 0,
                                        review.descriptionFeedbackUser
                                    )
                                }
                            )
                        }
                    }

                    // Отзывы от компании
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Ваши отзывы (${reviewsToUsers.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    if (reviewsToUsers.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Вы еще не оставляли отзывы", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(reviewsToUsers) { review ->
                            val userName = review.idUser?.let { userNames[it] } ?: "Пользователь"
                            ReviewToUserCard(
                                review = review,
                                userName = userName,
                                onViewProfile = { review.idUser?.let { onNavigateToUserProfile(it) } },
                                onViewVacancy = { review.idVacancy?.let { onNavigateToVacancyDetail(it) } },
                                onDelete = {
                                    selectedFeedbackId = review.idFeedbackCompany
                                    showDeleteDialog = true
                                },
                                onComplain = {
                                    onNavigateToCreateRequest(
                                        review.idFeedbackCompany ?: 0,
                                        review.descriptionCompany
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
fun ReviewFromUserCard(
    review: FeedbacksUser,
    onViewProfile: () -> Unit,
    onViewVacancy: () -> Unit,   // <-- ДОБАВЛЕНО
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
                    repeat(5) { index ->
                        Icon(
                            if (index < (review.ratingFeedbackUser ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                            null,
                            tint = if (index < (review.ratingFeedbackUser ?: 0)) Color(0xFFFFB74D) else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${review.ratingFeedbackUser}/5",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        review.dateFeedback ?: "",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }

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
                    Button(
                        onClick = onViewProfile,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Профиль", color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                review.descriptionFeedbackUser ?: "Нет описания",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (!review.statusFeedbackUser.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (review.statusFeedbackUser) {
                        "Новая" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        "Рассмотрена" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        review.statusFeedbackUser,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Кнопка "Вакансия"
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
fun ReviewToUserCard(
    review: FeedbacksCompany,
    userName: String,
    onViewProfile: () -> Unit,
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
                    userName,
                    color = Color(0xFF5399BC),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewProfile() }
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                review.dateFeedback ?: "",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Рейтинг: ${review.ratingStudent}/5",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                review.descriptionCompany ?: "Нет описания",
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

            if (!review.statusFeedbackCompany.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (review.statusFeedbackCompany) {
                        "Новый" -> Color(0xFFFF9800).copy(alpha = 0.2f)
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
        }
    }
}
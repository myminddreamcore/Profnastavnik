package org.example.project.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CurrentUser
import org.example.project.Models.FeedbacksCompany
import org.example.project.Models.Student
import kotlin.math.roundToInt

@Composable
fun UserProfileScreen(
    api: ApiClient,
    userId: Int,
    onBack: () -> Unit,
    onViewResume: () -> Unit,
    onNavigateToChat: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var student by remember { mutableStateOf<Student?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isTariffCorporate by remember { mutableStateOf(false) }
    var isLoadingTariff by remember { mutableStateOf(true) }

    // Отзывы о студенте
    var feedbacks by remember { mutableStateOf<List<FeedbacksCompany>>(emptyList()) }
    var isLoadingFeedbacks by remember { mutableStateOf(false) }
    var showAllFeedbacks by remember { mutableStateOf(false) }

    // Средний рейтинг студента (исправлено)
    val averageRating = remember(feedbacks) {
        if (feedbacks.isEmpty()) {
            0.0
        } else {
            val ratings = feedbacks.mapNotNull { it.ratingStudent }
            if (ratings.isEmpty()) {
                0.0
            } else {
                ratings.average()
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId <= 0) {
            isLoading = false
            return@LaunchedEffect
        }

        val result = api.getUserById(userId)
        if (result != null) {
            student = result
        }

        // Проверяем тариф компании
        val companyId = CurrentUser.id ?: 0
        val tariff = api.getCompanyTariff(companyId)
        isTariffCorporate = tariff?.idPrice == 3
        isLoadingTariff = false

        isLoading = false
    }

    // Функция загрузки отзывов
    fun loadFeedbacks() {
        if (feedbacks.isEmpty() && !isLoadingFeedbacks) {
            scope.launch {
                isLoadingFeedbacks = true
                api.getUserFeedbacksCompany(userId)?.let { result ->
                    feedbacks = result
                }
                isLoadingFeedbacks = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
    ) {
        if (isLoading || isLoadingTariff) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else if (student == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Error,
                        null,
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Профиль не найден",
                        color = Color.White
                    )
                }
            }
        } else {
            val s = student!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Верхняя панель
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        "Профиль сотрудника",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Аватар
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize().padding(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Карточка с информацией
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // ФИО
                        Text(
                            "${s.surnameStudent ?: ""} ${s.nameStudent ?: ""} ${s.patronymicStudent ?: ""}".trim(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // РЕЙТИНГ СТУДЕНТА (исправлено)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val displayRating = if (averageRating.isNaN() || averageRating.isInfinite()) 0.0 else averageRating
                            val roundedRating = (displayRating * 10).roundToInt() / 10.0
                            Text(
                                roundedRating.toString(),
                                color = Color(0xFFFFB74D),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "(${feedbacks.size} ${if (feedbacks.size == 1) "отзыв" else "отзывов"})",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Информация
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Email,
                                null,
                                tint = Color(0xFF5399BC),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                s.emailStudent,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!s.facultatyStudent.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    null,
                                    tint = Color(0xFF5399BC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    s.facultatyStudent ?: "",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (s.courseStudent != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Grade,
                                    null,
                                    tint = Color(0xFF5399BC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "${s.courseStudent} курс",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (s.universityStudent != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Business,
                                    null,
                                    tint = Color(0xFF5399BC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Университет: ${s.universityStudent}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (!s.birthdayStudent.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    null,
                                    tint = Color(0xFF5399BC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    s.birthdayStudent ?: "",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (!s.statusStudent.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    null,
                                    tint = Color(0xFF5399BC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    color = when (s.statusStudent) {
                                        "Активен" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        s.statusStudent ?: "",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // ========== БЛОК ОТЗЫВОВ ==========
                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Отзывы о студенте",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (feedbacks.size > 1) {
                                TextButton(
                                    onClick = {
                                        showAllFeedbacks = true
                                        loadFeedbacks()
                                    }
                                ) {
                                    Text(
                                        "Читать далее →",
                                        color = Color(0xFF5399BC),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Отображаем отзывы
                        if (feedbacks.isEmpty() && !isLoadingFeedbacks) {
                            Button(
                                onClick = { loadFeedbacks() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Загрузить отзывы", color = Color.White)
                            }
                        } else if (isLoadingFeedbacks) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            }
                        } else {
                            val displayFeedbacks = if (showAllFeedbacks) {
                                feedbacks
                            } else {
                                feedbacks.take(1)
                            }

                            displayFeedbacks.forEach { feedback ->
                                FeedbackCompanyCard(feedback = feedback)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (feedbacks.isEmpty()) {
                                Text(
                                    "Нет отзывов о студенте",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        if (showAllFeedbacks && feedbacks.size > 1) {
                            TextButton(
                                onClick = { showAllFeedbacks = false },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    "Скрыть отзывы",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка "Написать студенту" - только для корпоративного тарифа
                if (isTariffCorporate) {
                    GradientButton(
                        text = "Написать студенту",
                        icon = Icons.Default.Chat,
                        onClick = { onNavigateToChat(userId) },
                        gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Кнопка "Посмотреть резюме"
                GradientButton(
                    text = "Посмотреть резюме",
                    icon = Icons.Default.Visibility,
                    onClick = onViewResume,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF))
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FeedbackCompanyCard(feedback: FeedbacksCompany) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Верхняя строка: название компании + рейтинг
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Компания",
                    color = Color(0xFF5399BC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Рейтинг в звёздах
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rating = feedback.ratingStudent ?: 0
                    repeat(rating) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    repeat(5 - rating) {
                        Icon(
                            Icons.Default.StarBorder,
                            null,
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Текст отзыва
            Text(
                feedback.descriptionCompany ?: "Нет текста отзыва",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Статус отзыва
            if (!feedback.statusFeedbackCompany.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (feedback.statusFeedbackCompany) {
                        "Новый" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        "Опубликован" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        feedback.statusFeedbackCompany,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: List<Color>
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradient),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
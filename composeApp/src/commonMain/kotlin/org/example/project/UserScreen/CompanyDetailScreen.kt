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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.DirectorDTO
import org.example.project.Models.FeedbacksUser

@Composable
fun CompanyDetailScreen(
    api: ApiClient,
    companyId: Int,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var company by remember { mutableStateOf<DirectorDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Отзывы
    var allFeedbacks by remember { mutableStateOf<List<FeedbacksUser>>(emptyList()) }
    var showAllFeedbacks by remember { mutableStateOf(false) }
    var isLoadingFeedbacks by remember { mutableStateOf(false) }

    // Загружаем компанию
    LaunchedEffect(companyId) {
        val result = api.getCompanyCard(companyId)
        if (result != null) {
            company = result
        }
        isLoading = false
    }

    // Функция загрузки отзывов (вызывается из корутины)
    fun loadFeedbacks() {
        if (allFeedbacks.isEmpty() && !isLoadingFeedbacks) {
            scope.launch {
                isLoadingFeedbacks = true
                api.getCompanyFeedback(companyId)?.let { feedbacks ->
                    allFeedbacks = feedbacks
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
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            company?.let { data ->
                val director = data.director
                val rating = data.rating ?: 0.0

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
                            "ПРОФНаставник",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(modifier = Modifier.size(40.dp))
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Логотип компании
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
                                Icons.Default.Business,
                                null,
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize().padding(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Карточка с информацией о компании
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                director?.nameCompanyDirector ?: "Компания",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Рейтинг
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${(rating * 10).toInt() / 10.0}",
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
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Контактная информация
                            Divider(
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text(
                                "Контактная информация",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (!director?.emailDirector.isNullOrBlank()) {
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
                                        director?.emailDirector ?: "",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (!director?.phoneDirector.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Phone,
                                        null,
                                        tint = Color(0xFF5399BC),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        director?.phoneDirector ?: "",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (!director?.websiteDirector.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Language,
                                        null,
                                        tint = Color(0xFF5399BC),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        director?.websiteDirector ?: "",
                                        color = Color(0xFF5399BC),
                                        fontSize = 14.sp,
                                        modifier = Modifier.clickable {  }
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (!director?.cityDirector.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        null,
                                        tint = Color(0xFF5399BC),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        director?.cityDirector ?: "",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Руководитель
                            Divider(
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (director?.nameDirector != null || director?.surnameDirector != null) {
                                Text(
                                    "Руководитель",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                val fullName = listOf(
                                    director?.surnameDirector,
                                    director?.nameDirector,
                                    director?.patronymicDirector
                                ).filter { !it.isNullOrBlank() }.joinToString(" ")

                                if (fullName.isNotBlank()) {
                                    Text(
                                        fullName,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // Описание
                            Text(
                                "О компании",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                director?.descriptionDirector ?: "Нет описания",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            if (director?.dateCreatedDirector != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        null,
                                        tint = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Зарегистрирована: ${director.dateCreatedDirector}",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (director?.authenticationDirector == true) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Verified,
                                        null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Компания подтверждена",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 12.sp
                                    )
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
                                    "Отзывы о компании",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // Кнопка "Читать далее" если есть больше 1 отзыва
                                if (allFeedbacks.size > 1) {
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
                            if (allFeedbacks.isEmpty() && !isLoadingFeedbacks) {
                                // Если отзывы не загружены - показываем кнопку загрузки
                                Button(
                                    onClick = { loadFeedbacks() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Загрузить отзывы",
                                        color = Color.White
                                    )
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
                                // Показываем отзывы
                                val displayFeedbacks = if (showAllFeedbacks) {
                                    allFeedbacks
                                } else {
                                    allFeedbacks.take(1) // Только первый отзыв
                                }

                                displayFeedbacks.forEach { feedback ->
                                    FeedbackCard(feedback = feedback)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Если отзывов нет
                                if (allFeedbacks.isEmpty()) {
                                    Text(
                                        "Нет отзывов о компании",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }

                            // Если показаны все отзывы - кнопка "Скрыть"
                            if (showAllFeedbacks && allFeedbacks.size > 1) {
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

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// Компонент для отображения одного отзыва
@Composable
fun FeedbackCard(feedback: FeedbacksUser) {
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
            // Верхняя строка: имя пользователя + рейтинг
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                     "Студент",
                    color = Color(0xFF5399BC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Рейтинг в звёздах
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rating = feedback.ratingFeedbackUser ?: 0
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
                feedback.descriptionFeedbackUser ?: "Нет текста отзыва",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = if (true) Int.MAX_VALUE else 3
            )


        }
    }
}
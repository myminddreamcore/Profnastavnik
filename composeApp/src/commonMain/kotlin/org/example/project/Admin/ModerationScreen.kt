package org.example.project.Admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import org.example.project.Models.*
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun ModerationScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToCompanyProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToFeedbackDetail: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var moderationItems by remember { mutableStateOf<ModerationItems?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = listOf(
        "Все",
        "Пользователи",
        "Компании",
        "Вакансии",
        "Отзывы о студентах",
        "Отзывы о компаниях"
    )

    fun loadData() {
        scope.launch {
            isLoading = true
            val items = api.getModerationItems()
            moderationItems = items
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun updateStatus(
        type: String,
        id: Int,
        newStatus: String,
        currentStatus: String
    ) {
        if (currentStatus == newStatus) return

        scope.launch {
            val success = when (type) {
                "user" -> api.updateUserStatus(id, newStatus)
                "company" -> api.updateCompanyStatus(id, newStatus)
                "vacancy" -> api.updateVacancyStatus(id, newStatus)
                "feedbackCompany" -> api.updateFeedbackCompanyStatus(id, newStatus)
                "feedbackUser" -> api.updateFeedbackUserStatus(id, newStatus)
                else -> false
            }

            if (success) {
                snackbarHostState.showSnackbar("Статус обновлен: $newStatus")
                loadData()
            } else {
                snackbarHostState.showSnackbar("Ошибка обновления статуса")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Модерация") { target ->
                onNavigate(target)
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Верхняя панель
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Модерация",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { loadData() }) {
                            Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }

                    // Табы
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFF5399BC)
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Контент
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                    ) {
                        ModerationContent(
                            items = moderationItems,
                            selectedTab = selectedTab,
                            onNavigateToUserProfile = onNavigateToUserProfile,
                            onNavigateToCompanyProfile = onNavigateToCompanyProfile,
                            onNavigateToVacancyDetail = onNavigateToVacancyDetail,
                            onNavigateToFeedbackDetail = onNavigateToFeedbackDetail,
                            onUpdateStatus = { type, id, status, currentStatus ->
                                updateStatus(type, id, status, currentStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModerationContent(
    items: ModerationItems?,
    selectedTab: Int,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToCompanyProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToFeedbackDetail: (Int) -> Unit,
    onUpdateStatus: (String, Int, String, String) -> Unit
) {
    if (items == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Нет данных", color = Color.White.copy(alpha = 0.5f))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (selectedTab) {
            0 -> {
                // Все
                item { SectionHeaderr("Пользователи (${items.users.size})") }
                items(items.users) { user ->
                    ModerationUserCard(
                        user = user,
                        onClick = { onNavigateToUserProfile(user.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("user", user.id, status, user.status)
                        }
                    )
                }

                item { SectionHeaderr("Компании (${items.companies.size})") }
                items(items.companies) { company ->
                    ModerationCompanyCard(
                        company = company,
                        onClick = { onNavigateToCompanyProfile(company.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("company", company.id, status, company.status)
                        }
                    )
                }

                item { SectionHeaderr("Вакансии (${items.vacancies.size})") }
                items(items.vacancies) { vacancy ->
                    ModerationVacancyCard(
                        vacancy = vacancy,
                        onClick = { onNavigateToVacancyDetail(vacancy.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("vacancy", vacancy.id, status, vacancy.status)
                        }
                    )
                }

                item { SectionHeaderr("Отзывы о студентах (${items.feedbacksToUsers.size})") }
                items(items.feedbacksToUsers) { feedback ->
                    ModerationFeedbackCompanyCard(
                        feedback = feedback,
                        onClick = { onNavigateToFeedbackDetail(feedback.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("feedbackCompany", feedback.id, status, feedback.status)
                        }
                    )
                }

                item { SectionHeaderr("Отзывы о компаниях (${items.feedbacksToCompanies.size})") }
                items(items.feedbacksToCompanies) { feedback ->
                    ModerationFeedbackUserCard(
                        feedback = feedback,
                        onClick = { onNavigateToFeedbackDetail(feedback.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("feedbackUser", feedback.id, status, feedback.status)
                        }
                    )
                }
            }
            1 -> {
                // Только пользователи
                item { Text("Пользователи (${items.users.size})", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(items.users) { user ->
                    ModerationUserCard(
                        user = user,
                        onClick = { onNavigateToUserProfile(user.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("user", user.id, status, user.status)
                        }
                    )
                }
            }
            2 -> {
                // Только компании
                item { Text("Компании (${items.companies.size})", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(items.companies) { company ->
                    ModerationCompanyCard(
                        company = company,
                        onClick = { onNavigateToCompanyProfile(company.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("company", company.id, status, company.status)
                        }
                    )
                }
            }
            3 -> {
                // Только вакансии
                item { Text("Вакансии (${items.vacancies.size})", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(items.vacancies) { vacancy ->
                    ModerationVacancyCard(
                        vacancy = vacancy,
                        onClick = { onNavigateToVacancyDetail(vacancy.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("vacancy", vacancy.id, status, vacancy.status)
                        }
                    )
                }
            }
            4 -> {
                // Отзывы о студентах
                item { Text("Отзывы о студентах (${items.feedbacksToUsers.size})", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(items.feedbacksToUsers) { feedback ->
                    ModerationFeedbackCompanyCard(
                        feedback = feedback,
                        onClick = { onNavigateToFeedbackDetail(feedback.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("feedbackCompany", feedback.id, status, feedback.status)
                        }
                    )
                }
            }
            5 -> {
                // Отзывы о компаниях
                item { Text("Отзывы о компаниях (${items.feedbacksToCompanies.size})", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(items.feedbacksToCompanies) { feedback ->
                    ModerationFeedbackUserCard(
                        feedback = feedback,
                        onClick = { onNavigateToFeedbackDetail(feedback.id) },
                        onUpdateStatus = { status ->
                            onUpdateStatus("feedbackUser", feedback.id, status, feedback.status)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeaderr(title: String) {
    Text(
        title,
        color = Color(0xFF5399BC),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// ========== КАРТОЧКИ ДЛЯ МОДЕРАЦИИ ==========

@Composable
fun ModerationUserCard(
    user: ModerationUser,
    onClick: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                user.fullName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                user.email,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            if (!user.university.isNullOrBlank()) {
                Text(
                    user.university,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            user.course?.let {
                Text(
                    "Курс: $it",
                    color = Color(0xFF5399BC),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusActionButton(
                    label = "Активен",
                    color = Color(0xFF4CAF50),
                    onClick = { onUpdateStatus("Активен") }
                )
                StatusActionButton(
                    label = "Удален",
                    color = Color(0xFFB71C1C),
                    onClick = { onUpdateStatus("Удален") }
                )
            }
        }
    }
}

@Composable
fun ModerationCompanyCard(
    company: ModerationCompany,
    onClick: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                company.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                company.email,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            if (!company.city.isNullOrBlank()) {
                Text(
                    "Город: ${company.city}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusActionButton(
                    label = "Активен",
                    color = Color(0xFF4CAF50),
                    onClick = { onUpdateStatus("Активен") }
                )
                StatusActionButton(
                    label = "Удален",
                    color = Color(0xFFB71C1C),
                    onClick = { onUpdateStatus("Удален") }
                )
            }
        }
    }
}

@Composable
fun ModerationVacancyCard(
    vacancy: ModerationVacancy,
    onClick: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                vacancy.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Компания: ${vacancy.companyName}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            if (!vacancy.description.isNullOrBlank()) {
                Text(
                    vacancy.description,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusActionButton(
                    label = "Активна",
                    color = Color(0xFF4CAF50),
                    onClick = { onUpdateStatus("Активна") }
                )
                StatusActionButton(
                    label = "Архив",
                    color = Color(0xFFB71C1C),
                    onClick = { onUpdateStatus("Архив") }
                )
            }
        }
    }
}
@Composable
fun ModerationFeedbackCompanyCard(
    feedback: ModerationFeedbackCompany,
    onClick: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Отзыв о студенте",
                    color = Color(0xFFFFB74D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                // ДАТА
                if (!feedback.date.isNullOrBlank()) {
                    Text(
                        feedback.date,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }
            }
            Text(
                "Студент: ${feedback.studentName}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Компания: ${feedback.companyName}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Text(
                "Рейтинг: ${feedback.rating} ★",
                color = Color(0xFFFFB74D),
                fontSize = 13.sp
            )
            Text(
                feedback.description,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusActionButton(
                    label = "Одобрено",
                    color = Color(0xFF4CAF50),
                    onClick = { onUpdateStatus("Одобрено") }
                )
                StatusActionButton(
                    label = "Удалено",
                    color = Color(0xFFB71C1C),
                    onClick = { onUpdateStatus("Удалено") }
                )
            }
        }
    }
}

// ModerationFeedbackUserCard
@Composable
fun ModerationFeedbackUserCard(
    feedback: ModerationFeedbackUser,
    onClick: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Отзыв о компании",
                    color = Color(0xFFFFB74D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                // ДАТА
                if (!feedback.date.isNullOrBlank()) {
                    Text(
                        feedback.date,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }
            }
            Text(
                "Студент: ${feedback.studentName}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Компания: ${feedback.companyName}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Text(
                "Рейтинг: ${feedback.rating} ★",
                color = Color(0xFFFFB74D),
                fontSize = 13.sp
            )
            Text(
                feedback.description,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusActionButton(
                    label = "Одобрен",
                    color = Color(0xFF4CAF50),
                    onClick = { onUpdateStatus("Одобрен") }
                )
                StatusActionButton(
                    label = "Удален",
                    color = Color(0xFFB71C1C),
                    onClick = { onUpdateStatus("Удален") }
                )
            }
        }
    }
}

@Composable
fun StatusActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
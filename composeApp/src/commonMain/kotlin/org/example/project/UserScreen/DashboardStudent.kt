package org.example.project.UserScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CurrentUser
import org.example.project.Models.Student
import org.example.project.Models.UserVacancies
import org.example.project.Models.Listresponcies
import org.example.project.Models.UserPrice
val NavItemColor = Color(0xFF5399BC)

@Composable
fun DashboardStudent(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToResponses: (String) -> Unit,
    onNavigateToTariffs: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit
) {
    var vacancies by remember { mutableStateOf<List<UserVacancies>>(emptyList()) }
    var recommendations by remember { mutableStateOf<List<Listresponcies>>(emptyList()) }
    var messageCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var userPrice by remember { mutableStateOf<UserPrice?>(null) }
    var isLoadingPrice by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = Student(idStudent = CurrentUser.id ?: 0, emailStudent = CurrentUser.email ?: "")
        val fetchedVacancies = api.getVacancies(user)
        val fetchedRecommendations = api.getRecommendationVacancy(CurrentUser.id ?: 0)
        val fetchedMessages = api.getCountUsermessages(user)
        val fetchedUserPrice = api.getUserPrice(CurrentUser.id ?: 0)

        if (fetchedVacancies != null) vacancies = fetchedVacancies
        if (fetchedRecommendations != null) recommendations = fetchedRecommendations
        if (fetchedMessages != null) messageCount = fetchedMessages
        if (fetchedUserPrice != null) userPrice = fetchedUserPrice
        isLoadingPrice = false
        isLoading = false
    }

    val isPremium = userPrice?.idPrices != 1
    val showRecommendations = isPremium

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Дашборд") { target ->
                onNavigate(target)
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(paddingValues)
        ) {
            MainDashboardContent(
                vacancies = vacancies,
                recommendations = recommendations,
                messageCount = messageCount,
                isLoading = isLoading,
                isLoadingPrice = isLoadingPrice,
                showRecommendations = showRecommendations,
                onNavigateToResponses = onNavigateToResponses,
                onNavigateToVacancyDetail = onNavigateToVacancyDetail,
                onNavigateToTariffs = onNavigateToTariffs,
                onNavigateToChats = onNavigateToChats

            )
        }
    }
}

@Composable
fun MainDashboardContent(
    vacancies: List<UserVacancies>,
    recommendations: List<Listresponcies>,
    messageCount: Int,
    isLoading: Boolean,
    isLoadingPrice: Boolean,
    onNavigateToChats: () -> Unit,
    showRecommendations: Boolean,
    onNavigateToTariffs: () -> Unit,
    onNavigateToResponses: (String) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier.clickable { onNavigateToChats() }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                if (messageCount > 0) {
                    Surface(
                        color = Color(0xFFB71C1C),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                    ) {}
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusItem(
                label = "рассматривается",
                icon = Icons.Default.Description,
                gradientColors = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF)),
                onClick = { onNavigateToResponses("consider") }
            )
            StatusItem(
                label = "приглашено",
                icon = Icons.Default.AssignmentTurnedIn,
                gradientColors = listOf(Color(0xFF4A90E2), Color(0xFF00E676)),
                onClick = { onNavigateToResponses("invited") }
            )
            StatusItem(
                label = "отклонено",
                icon = Icons.Default.Close,
                gradientColors = listOf(Color(0xFF4A90E2), Color(0xFFB71C1C)),
                onClick = { onNavigateToResponses("rejected") }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("Мои стажировки", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (vacancies.isEmpty()) {
                    Text("У вас пока нет активных стажировок", color = Color.White.copy(alpha = 0.6f))
                } else {
                    vacancies.forEach { vacancy ->
                        VacancyCard(vacancy)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (!isLoadingPrice) {
            Text("Рекомендации", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (!showRecommendations) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Рекомендации доступны в тарифе Профи",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Подключите премиум тариф, чтобы получать персональные рекомендации вакансий",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToTariffs() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Подробнее о тарифах", color = Color.White)
                        }
                    }
                }
            } else if (showRecommendations && recommendations.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    recommendations.forEach { recommendation ->
                        RecommendationCard(
                            item = recommendation,
                            onClick = { onNavigateToVacancyDetail(recommendation.idVacancy ?: 0) }
                        )
                    }
                }
            } else {
                Text(
                    "Нет рекомендаций",
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun RecommendationCard(item: Listresponcies, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                item.nameVacancy ?: "Вакансия",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${item.zenStart} - ${item.zenEnd} ${item.currency}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Длительность: ${item.time}",
                color = Color(0xFF5399BC),
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun CustomBottomNavigation(
    currentScreen: String,
    onItemSelected: (String) -> Unit
) {
    // Получаем роль пользователя
    val userRole = CurrentUser.role ?: "student"

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF5399BC).copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(
                "Дашборд",
                Icons.Default.Dashboard,
                currentScreen == "Дашборд"
            ) {
                if (currentScreen != "Дашборд") onItemSelected("Дашборд")
            }

            NavigationItem(
                "Поиск",
                Icons.Default.Search,
                currentScreen == "Поиск"
            ) {
                if (currentScreen != "Поиск") onItemSelected("Поиск")
            }

            if (userRole == "admin") {
                NavigationItem(
                    "Модерация",
                    Icons.Default.Gavel,
                    currentScreen == "Модерация"
                ) {
                    if (currentScreen != "Модерация") onItemSelected("Модерация")
                }
            }
            if (userRole != "admin") {
                NavigationItem(
                    "Профиль",
                    Icons.Default.Person,
                    currentScreen == "Профиль"
                ) {
                    if (currentScreen != "Профиль") onItemSelected("Профиль")
                }

                NavigationItem(
                    "Настройки",
                    Icons.Default.Settings,
                    currentScreen == "Настройки"
                ) {
                    if (currentScreen != "Настройки") onItemSelected("Настройки")
                }
            }

        }
    }
}

@Composable
fun NavigationItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {

    val color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun VacancyCard(vacancy: UserVacancies) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(190.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BgGradientStart
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(vacancy.nameVacancy, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
            Text(vacancy.nameCompany ?: "", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text("${vacancy.dateEnd?.toInt() ?: 0} дней до конца", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatusItem(label: String, icon: ImageVector, gradientColors: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(brush = Brush.verticalGradient(gradientColors), shape = RoundedCornerShape(24.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = Color.White, fontSize = 10.sp)
        }
    }
}
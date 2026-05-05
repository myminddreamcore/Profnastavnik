package org.example.project

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.API.ApiClient
import org.example.project.Models.CurrentUser
import org.example.project.Models.Student
import org.example.project.Models.UserVacancies

val NavItemColor = Color(0xFF5399BC)
@Composable
fun DashboardStudent(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToResponses: (String) -> Unit
) {
    var vacancies by remember { mutableStateOf<List<UserVacancies>>(emptyList()) }
    var messageCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = Student(idStudent = CurrentUser.id ?: 0, emailStudent = CurrentUser.email ?: "")
        val fetchedVacancies = api.getVacancies(user)
        val fetchedMessages = api.getCountUsermessages(user)
        if (fetchedVacancies != null) vacancies = fetchedVacancies
        if (fetchedMessages != null) messageCount = fetchedMessages
        isLoading = false
    }

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
            MainDashboardContent(vacancies, messageCount, isLoading, onNavigateToResponses = onNavigateToResponses)
        }
    }
}

@Composable
fun MainDashboardContent(vacancies: List<UserVacancies>, messageCount: Int, isLoading: Boolean, onNavigateToResponses: (String) -> Unit) {
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

            Box {
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

        Text("Рекомендации", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(180.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
@Composable
fun CustomBottomNavigation(
    currentScreen: String,
    onItemSelected: (String) -> Unit
) {
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
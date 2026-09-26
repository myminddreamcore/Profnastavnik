package org.example.project.Admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun DashboardAdmin(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToStudentProfile: (Int) -> Unit,
    onNavigateToCompanyProfile: (Int) -> Unit,
    onNavigateToChats: () -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit = {}
) {
    var topCompanies by remember { mutableStateOf<List<TopCompanyDTO>>(emptyList()) }
    var topStudents by remember { mutableStateOf<List<TopStudentAdminDTO>>(emptyList()) }
    var topUniversities by remember { mutableStateOf<List<TopUniversityDTO>>(emptyList()) }
    var paymentsChart by remember { mutableStateOf<List<PaymentChartDTO>>(emptyList()) }
    var studentPaymentsChart by remember { mutableStateOf<List<StudentPaymentChartDTO>>(emptyList()) }
    var companyPaymentsChart by remember { mutableStateOf<List<CompanyPaymentChartDTO>>(emptyList()) }
    var newUsersChart by remember { mutableStateOf<List<NewUsersChartDTO>>(emptyList()) }
    var userChurnChart by remember { mutableStateOf<List<UserChurnChartDTO>>(emptyList()) }
    var successInternshipsChart by remember { mutableStateOf<List<SuccessInternshipChartDTO>>(emptyList()) }
    var stats by remember { mutableStateOf<DashboardStatsDTO?>(null) }
    var messageCountt: Int? by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isLoading = true

            val companies = api.getTopCompanies()
            if (companies != null) {
                topCompanies = companies
                println("TopCompanies: ${companies.size}")
            }

            val students = api.getTopStudentsAdmin()
            if (students != null) {
                topStudents = students
                println("TopStudents: ${students.size}")
            }

            val universities = api.getTopUniversities()
            if (universities != null) {
                topUniversities = universities
                println("TopUniversities: ${universities.size}")
            }

            val payments = api.getPaymentsChart()
            if (payments != null) {
                paymentsChart = payments
                println("PaymentsChart: ${payments.size}")
                payments.forEach { println("  $it") }
            }

            val studentPayments = api.getStudentPaymentsChart()
            if (studentPayments != null) {
                studentPaymentsChart = studentPayments
                println("StudentPaymentsChart: ${studentPayments.size}")
            }

            val companyPayments = api.getCompanyPaymentsChart()
            if (companyPayments != null) {
                companyPaymentsChart = companyPayments
                println("CompanyPaymentsChart: ${companyPayments.size}")
            }

            val newUsers = api.getNewUsersChart()
            if (newUsers != null) {
                newUsersChart = newUsers
                println("NewUsersChart: ${newUsers.size}")
            }

            val churn = api.getUserChurnChart()
            if (churn != null) {
                userChurnChart = churn
                println("UserChurnChart: ${churn.size}")
            }

            val internships = api.getSuccessInternshipsChart()
            if (internships != null) {
                successInternshipsChart = internships
                println("SuccessInternshipsChart: ${internships.size}")
            }

            val dashboardStats = api.getDashboardStats()
            if (dashboardStats != null) {
                stats = dashboardStats
                println("DashboardStats: $dashboardStats")
            }
            var messageCount = api.getCountAdminmessages(CurrentUser.email)
            messageCountt = messageCount
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Иконка луны (переключение темы)
                            IconButton(
                                onClick = onToggleTheme,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = "Темная тема",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // Колокольчик (уведомления)
                            Box(
                                modifier = Modifier.clickable { onNavigateToChats() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                messageCountt?.let {
                                    if (it > 0) {
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
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (stats != null) {
                        StatsRow(stats!!)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    SectionHeader("Топ-3 компании")
                    HorizontalScrollRow {
                        if (topCompanies.isEmpty()) {
                            Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                        } else {
                            topCompanies.forEach { company ->
                                TopCompanyCard(
                                    company = company,
                                    onClick = { onNavigateToCompanyProfile(company.companyId) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("Топ-3 стажера")
                    HorizontalScrollRow {
                        if (topStudents.isEmpty()) {
                            Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                        } else {
                            topStudents.forEach { student ->
                                TopStudentAdminCard(
                                    student = student,
                                    onClick = { onNavigateToStudentProfile(student.studentId) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("Топ-3 университета")
                    HorizontalScrollRow {
                        if (topUniversities.isEmpty()) {
                            Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                        } else {
                            topUniversities.forEach { university ->
                                TopUniversityCard(university)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("График оплат (все)")
                    if (paymentsChart.isEmpty()) {
                        Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                    } else {
                        val totalYear = paymentsChart.sumOf { it.amount.toInt() }
                        val paymentsByMonth = paymentsChart
                            .groupBy { it.month }
                            .mapValues { entry ->
                                entry.value.sumOf { it.amount.toInt() }
                            }
                        ChartCard(
                            title = "Оплаты по месяцам",
                            data = paymentsByMonth,
                            totalYear = totalYear,
                            type = "payments"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("График оплат (студенты)")
                    if (studentPaymentsChart.isEmpty()) {
                        Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                    } else {
                        val totalYear = studentPaymentsChart.sumOf { it.amount.toInt() }
                        val studentPaymentsByMonth = studentPaymentsChart
                            .groupBy { it.month }
                            .mapValues { entry ->
                                entry.value.sumOf { it.amount.toInt() }
                            }
                        ChartCard(
                            title = "Оплаты студентов по месяцам",
                            data = studentPaymentsByMonth,
                            totalYear = totalYear,
                            type = "payments"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("График оплат (компании)")
                    if (companyPaymentsChart.isEmpty()) {
                        Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                    } else {
                        val totalYear = companyPaymentsChart.sumOf { it.amount.toInt() }
                        val companyPaymentsByMonth = companyPaymentsChart
                            .groupBy { it.month }
                            .mapValues { entry ->
                                entry.value.sumOf { it.amount.toInt() }
                            }
                        ChartCard(
                            title = "Оплаты компаний по месяцам",
                            data = companyPaymentsByMonth,
                            totalYear = totalYear,
                            type = "payments"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("График новых пользователей")
                    if (newUsersChart.isEmpty()) {
                        Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                    } else {
                        val totalYear = newUsersChart.sumOf { it.count }
                        ChartCard(
                            title = "Новые пользователи по месяцам",
                            data = newUsersChart.associate { it.month to it.count },
                            totalYear = totalYear,
                            type = "users"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("График ухода пользователей")
                    if (userChurnChart.isEmpty()) {
                        Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                    } else {
                        val totalYear = userChurnChart.sumOf { it.count }
                        ChartCard(
                            title = "Уход пользователей по месяцам",
                            data = userChurnChart.associate { it.month to it.count },
                            totalYear = totalYear,
                            type = "churn"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    SectionHeader("График успешных стажировок")
                    if (successInternshipsChart.isEmpty()) {
                        Text("Нет данных", color = Color.White.copy(alpha = 0.6f))
                    } else {
                        val totalYear = successInternshipsChart.sumOf { it.count }
                        ChartCard(
                            title = "Успешные стажировки по месяцам",
                            data = successInternshipsChart.associate { it.month to it.count },
                            totalYear = totalYear,
                            type = "internships"
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // ========== КНОПКА ВЫХОДА ==========
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB71C1C).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFFB71C1C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Выйти из аккаунта",
                            color = Color(0xFFB71C1C),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Диалог подтверждения выхода
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Выход из аккаунта",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Вы уверены, что хотите выйти?",
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C)
                    )
                ) {
                    Text("Выйти", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Отмена", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E2E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}
@Composable
fun TopCompanyCard(
    company: TopCompanyDTO,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(180.dp)
            .clickable { onClick() }, // <-- ДОБАВЛЕН КЛИК
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF5399BC).copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                company.companyName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "${company.rating}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Завершено: ${company.completedInternships}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// Карточка студента (с кликом)
@Composable
fun TopStudentAdminCard(
    student: TopStudentAdminDTO,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(210.dp)
            .clickable { onClick() }, // <-- ДОБАВЛЕН КЛИК
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF7B61FF).copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                student.fullName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                student.universityName,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip(
                    label = "Стажировки",
                    value = "${student.completedInternships}",
                    color = Color(0xFF00E676)
                )
                StatChip(
                    label = "Навыки",
                    value = "${student.skillsCount}",
                    color = Color(0xFF4A90E2)
                )
                StatChip(
                    label = "Профессии",
                    value = "${student.professionsCount}",
                    color = Color(0xFF7B61FF)
                )
            }
        }
    }
}


@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun HorizontalScrollRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

// СТАТИСТИКА С ГОРИЗОНТАЛЬНОЙ ПРОКРУТКОЙ
@Composable
fun StatsRow(stats: DashboardStatsDTO) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StatItem("Пользователи", stats.totalUsers.toString(), Color(0xFF4FC3F7))
        StatItem("Студенты", stats.totalStudents.toString(), Color(0xFF81C784))
        StatItem("Компании", stats.totalCompanies.toString(), Color(0xFFFFB74D))
        StatItem("Стажировки", stats.totalInternships.toString(), Color(0xFFCE93D8))
        StatItem("Завершено", stats.completedInternships.toString(), Color(0xFF4DD0E1))
        StatItem("Успешность", "${stats.successRate}%", Color(0xFFF06292))
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Surface(
        modifier = Modifier
            .width(70.dp) // Увеличено с 55.dp
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 10.sp, // Увеличено с 9.sp
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// Карточка компании
@Composable
fun TopCompanyCard(company: TopCompanyDTO) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF5399BC).copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                company.companyName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "${company.rating}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Завершено: ${company.completedInternships}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// Карточка студента
@Composable
fun TopStudentAdminCard(student: TopStudentAdminDTO) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(210.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF7B61FF).copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                student.fullName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                student.universityName,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip(
                    label = "Стажировки",
                    value = "${student.completedInternships}",
                    color = Color(0xFF00E676)
                )
                StatChip(
                    label = "Навыки",
                    value = "${student.skillsCount}",
                    color = Color(0xFF4A90E2)
                )
                StatChip(
                    label = "Профессии",
                    value = "${student.professionsCount}",
                    color = Color(0xFF7B61FF)
                )
            }
        }
    }
}

// Карточка университета (увеличена)
@Composable
fun TopUniversityCard(university: TopUniversityDTO) {
    Card(
        modifier = Modifier
            .width(180.dp) // Увеличено с 160.dp
            .height(160.dp), // Увеличено с 150.dp
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                Icons.Default.School,
                contentDescription = null,
                tint = Color(0xFFFFB74D),
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = university.universityName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp, // Увеличено с 13.sp
                maxLines = 3,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Студентов: ${university.studentCount}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp, // Увеличено с 12.sp
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

// Карточка графика
@Composable
fun ChartCard(
    title: String,
    data: Map<String, Int>,
    totalYear: Int,
    type: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Итого за год
            val currencySymbol = if (type == "payments") "₽" else ""
            Text(
                "Итого за год: $totalYear $currencySymbol",
                color = Color(0xFFFFB74D),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Горизонтальный скролл для 12 месяцев
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val maxValue = data.values.maxOrNull()?.toFloat() ?: 1f
                val entries = data.entries.toList()

                if (entries.isEmpty()) {
                    Text(
                        "Нет данных для отображения",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                } else {
                    entries.forEach { (month, count) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Сумма над столбцом
                            Text(
                                text = count.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height((count.toFloat() / maxValue * 100).dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color(0xFF5399BC),
                                                Color(0xFF7B61FF)
                                            )
                                        ),
                                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                month,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}
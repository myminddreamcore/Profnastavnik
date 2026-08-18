package org.example.project.CompanyScreen

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CardVacancy
import org.example.project.Models.CurrentUser
import org.example.project.Models.Director
import org.example.project.Models.Listresponcies
import org.example.project.Models.ResponciesDTO
import org.example.project.Models.TopStudentDTO
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun DashboardCompany(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToChats: () -> Unit,
    onCreateVacancy: () -> Unit,
    onViewResponses: () -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit
) {
    var vacancies by remember { mutableStateOf<List<Listresponcies>>(emptyList()) }
    var responses by remember { mutableStateOf<List<ResponciesDTO>>(emptyList()) }
    var messageCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var selectedVacancyId by remember { mutableStateOf<Int?>(null) }
    var topStudents by remember { mutableStateOf<List<TopStudentDTO>>(emptyList()) }
    var isLoadingTopStudents by remember { mutableStateOf(true) }
    var isTariffCorporate by remember { mutableStateOf(false) }
    var vacancyDetails by remember { mutableStateOf<Map<Int, CardVacancy>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isLoading = true
            isLoadingTopStudents = true
            val director = Director(idDirector = CurrentUser.id ?: 0)

            // Загружаем тариф
            val tariff = api.getCompanyTariff(CurrentUser.id ?: 0)
            isTariffCorporate = tariff?.idPrice == 3

            val fetchedMessages = api.getCountCompanymessages(director)
            val fetchedVacancies = api.getCompanyVacancies(director)
            val fetchedResponses = api.getCompanyResponcies(director)

            if (fetchedMessages != null) messageCount = fetchedMessages
            if (fetchedVacancies != null) {
                vacancies = fetchedVacancies

                val detailsMap = mutableMapOf<Int, CardVacancy>()
                fetchedVacancies.forEach { vacancy ->
                    vacancy.idVacancy?.let { id ->
                        val detail = api.getVacancy(id)
                        if (detail != null) {
                            detailsMap[id] = detail
                        }
                    }
                }
                vacancyDetails = detailsMap

                vacancies = fetchedVacancies.sortedBy { vacancy ->
                    val detail = vacancy.idVacancy?.let { vacancyDetails[it] }
                    val status = detail?.vacancy?.statusVacancy
                    when (status) {
                        "Активна" -> 0
                        "На модерации" -> 1
                        else -> 2
                    }
                }
            }
            if (fetchedResponses != null) responses = fetchedResponses

            // Загружаем топ-студентов только для корпоративного тарифа
            if (isTariffCorporate) {
                val top = api.getTopStudents(CurrentUser.id ?: 0)
                if (top != null) {
                    topStudents = top
                }
            }
            isLoadingTopStudents = false
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun archiveVacancy(vacancyId: Int) {
        scope.launch {
            val success = api.archiveVacancy(vacancyId)
            if (success) {
                loadData()
            }
            showArchiveDialog = false
            selectedVacancyId = null
        }
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
            CompanyDashboardContent(
                api = api,
                vacancies = vacancies,
                vacancyDetails = vacancyDetails,
                responses = responses,
                topStudents = topStudents,
                messageCount = messageCount,
                isLoading = isLoading,
                isLoadingTopStudents = isLoadingTopStudents,
                isTariffCorporate = isTariffCorporate,
                onNavigateToChats = onNavigateToChats,
                onCreateVacancy = onCreateVacancy,
                onViewResponses = onViewResponses,
                onNavigateToVacancyDetail = onNavigateToVacancyDetail,
                onNavigateToUserProfile = onNavigateToUserProfile,
                onArchiveVacancy = { vacancyId ->
                    selectedVacancyId = vacancyId
                    showArchiveDialog = true
                }
            )
        }
    }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = {
                Text(
                    "Архивировать вакансию",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Вы уверены, что хотите архивировать эту вакансию?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Архивные вакансии не будут отображаться в списке активных.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = {
                        selectedVacancyId?.let { archiveVacancy(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C)
                    )
                ) {
                    Text("Архивировать", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showArchiveDialog = false
                        selectedVacancyId = null
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
fun CompanyDashboardContent(
    api: ApiClient,
    vacancies: List<Listresponcies>,
    vacancyDetails: Map<Int, CardVacancy>,
    responses: List<ResponciesDTO>,
    topStudents: List<TopStudentDTO>,
    messageCount: Int,
    isLoading: Boolean,
    isLoadingTopStudents: Boolean,
    isTariffCorporate: Boolean,
    onNavigateToChats: () -> Unit,
    onCreateVacancy: () -> Unit,
    onViewResponses: () -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onArchiveVacancy: (Int) -> Unit
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompanyActionItem(
                label = "Создать вакансию",
                icon = Icons.Default.NoteAdd,
                gradientColors = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF)),
                onClick = onCreateVacancy,
                modifier = Modifier.weight(1f)
            )

            CompanyActionItem(
                label = "Посмотреть отклики",
                icon = Icons.Default.Assignment,
                gradientColors = listOf(Color(0xFF4A90E2), Color(0xFF00E676)),
                onClick = onViewResponses,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("Мои вакансии", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                    Text("У вас пока нет активных вакансий", color = Color.White.copy(alpha = 0.6f))
                } else {
                    vacancies.forEach { vacancy ->
                        CompanyVacancyCard(
                            api = api,
                            vacancyId = vacancy.idVacancy ?: 0,
                            vacancyDetail = vacancy.idVacancy?.let { vacancyDetails[it] },
                            onClick = { onNavigateToVacancyDetail(vacancy.idVacancy ?: 0) },
                            onArchive = { onArchiveVacancy(vacancy.idVacancy ?: 0) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))



        // Топ-10 студентов (только для корпоративного тарифа)
        if (isTariffCorporate) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Топ-10 лучших студентов",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color(0xFFFFB74D).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Корпоративный тариф",
                        color = Color(0xFFFFB74D),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoadingTopStudents) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (topStudents.isEmpty()) {
                Text(
                    "Нет данных для отображения",
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    topStudents.forEach { student ->
                        TopStudentCard(
                            student = student,
                            onClick = { onNavigateToUserProfile(student.idStudent) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
@Composable
fun TopStudentCard(
    student: TopStudentDTO,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)  // Уменьшил ширину
            .height(240.dp) // Уменьшил высоту
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp) // Уменьшил отступы
        ) {
            // Рейтинг
            Surface(
                color = Color(0xFFFFB74D).copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(28.dp) // Уменьшил размер
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "${student.totalScore.toInt()}",
                        color = Color(0xFFFFB74D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ФИО
            Text(
                student.fullName ?: "Студент",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Курс и университет в одну строку
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (student.courseStudent != null) {
                    Text(
                        "${student.courseStudent} курс",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
                if (!student.universityStudent.isNullOrBlank()) {
                    Text(
                        "• ${student.universityStudent ?: ""}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Статистика в ряд (2 строки по 2 чипа)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SmallStatChip(
                        label = "Совместимость",
                        value = "${student.vacancyCompatibility.toInt()}%",
                        color = Color(0xFF7B61FF)
                    )
                    SmallStatChip(
                        label = "Стажировки",
                        value = "${student.completedInternships}",
                        color = Color(0xFF00E676)
                    )

                }

            }

            Spacer(modifier = Modifier.weight(1f))

            // Навыки (первые 2)
            if (!student.skills.isNullOrEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    student.skills?.take(2)?.forEach { skill ->
                        Surface(
                            color = Color(0xFF5399BC).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                skill,
                                color = Color.White,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if ((student.skills?.size ?: 0) > 2) {
                        Text(
                            "+${(student.skills?.size ?: 0) - 2}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmallStatChip(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                value,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 7.sp
            )
        }
    }
}

@Composable
fun CompanyActionItem(
    label: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .background(brush = Brush.verticalGradient(gradientColors), shape = RoundedCornerShape(24.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CompanyVacancyCard(
    api: ApiClient,
    vacancyId: Int,
    vacancyDetail: CardVacancy?,
    onClick: () -> Unit,
    onArchive: () -> Unit
) {
    val vacancy = vacancyDetail?.vacancy
    val isOnModeration = vacancy?.statusVacancy == "На модерации"
    val isArchived = vacancy?.statusVacancy == "Архив"

    if (isArchived) return

    if (vacancyDetail == null) {
        Box(
            modifier = Modifier
                .width(170.dp)
                .height(if (isOnModeration) 210.dp else 190.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        }
        return
    }

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(if (isOnModeration) 210.dp else 190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isOnModeration) {
                Surface(
                    color = Color(0xFFFF9800).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        "На модерации",
                        color = Color(0xFFFF9800),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                vacancy?.nameVacancy ?: "Вакансия",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${vacancy?.zenStartVacancy} - ${vacancy?.zenEndVacancy} ${vacancyDetail.currency}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Длительность: ${vacancy?.timeVacancy}",
                color = Color(0xFF5399BC),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            if (!isOnModeration && !isArchived) {
                Button(
                    onClick = onArchive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C).copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Archive,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "В архив",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResponseCard(
    item: ResponciesDTO,
    onViewProfile: () -> Unit,
    onViewVacancy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                item.fIO ?: "Студент",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                item.nameVacancy ?: "Вакансия",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.description ?: "Нет описания",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradientButton(
                    text = "Профиль",
                    icon = Icons.Default.Person,
                    onClick = onViewProfile,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF)),
                    modifier = Modifier.weight(1f)
                )

                GradientButton(
                    text = "Вакансия",
                    icon = Icons.Default.Work,
                    onClick = onViewVacancy,
                    gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF)),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradient),
                    shape = RoundedCornerShape(12.dp)
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
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
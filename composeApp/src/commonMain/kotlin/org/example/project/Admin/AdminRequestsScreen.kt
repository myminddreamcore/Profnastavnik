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
import org.example.project.Models.RequestsCompany
import org.example.project.Models.RequestsUser
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun AdminRequestsScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToCompanyProfile: (Int) -> Unit,
    onNavigateToFeedbackDetail: (Int) -> Unit
) {
    var userRequests by remember { mutableStateOf<List<RequestsUser>>(emptyList()) }
    var companyRequests by remember { mutableStateOf<List<RequestsCompany>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) } // 0 - жалобы на отзывы, 1 - пустые жалобы

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val tabs = listOf("Жалобы на отзывы", "Пустые жалобы")

    fun loadData() {
        scope.launch {
            isLoading = true

            val users = api.getAllUserRequests()
            if (users != null) userRequests = users

            val companies = api.getAllCompanyRequests()
            if (companies != null) companyRequests = companies

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun updateStatus(requestId: Int, status: String, type: String) {
        scope.launch {
            val success = api.updateRequestStatus(requestId, status, type)
            if (success) {
                snackbarHostState.showSnackbar("Статус обновлен")
                loadData()
            } else {
                snackbarHostState.showSnackbar("Ошибка обновления")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Жалобы") { target ->
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
                        "Жалобы",
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
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        val items = when (selectedTab) {
                            0 -> {
                                // Жалобы на отзывы (где есть idFeedback)
                                val all = mutableListOf<Pair<Any, String>>()
                                userRequests.filter { it.idFeedback != null }.forEach {
                                    all.add(Pair(it, "user"))
                                }
                                companyRequests.filter { it.idFeedback != null }.forEach {
                                    all.add(Pair(it, "company"))
                                }
                                all
                            }
                            else -> {
                                // Пустые жалобы (где idFeedback == null)
                                val all = mutableListOf<Pair<Any, String>>()
                                userRequests.filter { it.idFeedback == null }.forEach {
                                    all.add(Pair(it, "user"))
                                }
                                companyRequests.filter { it.idFeedback == null }.forEach {
                                    all.add(Pair(it, "company"))
                                }
                                all
                            }
                        }

                        if (items.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.ReportOff,
                                        null,
                                        tint = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Нет жалоб на модерации",
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(items) { (item, type) ->
                                    when (type) {
                                        "user" -> AdminRequestUserCard(
                                            request = item as RequestsUser,
                                            onViewProfile = { onNavigateToUserProfile(item.idStudent ?: 0) },
                                            onViewFeedback = { item.idFeedback?.let { onNavigateToFeedbackDetail(it) } },
                                            onApprove = {
                                                updateStatus(item.idRequest, "Одобрено", "user")
                                            },
                                            onReject = {
                                                updateStatus(item.idRequest, "Отклонено", "user")
                                            }
                                        )
                                        "company" -> AdminRequestCompanyCard(
                                            request = item as RequestsCompany,
                                            onViewCompany = { onNavigateToCompanyProfile(item.idCompany ?: 0) },
                                            onViewFeedback = { item.idFeedback?.let { onNavigateToFeedbackDetail(it) } },
                                            onApprove = {
                                                updateStatus(item.idRequest, "Одобрено", "company")
                                            },
                                            onReject = {
                                                updateStatus(item.idRequest, "Отклонено", "company")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRequestUserCard(
    request: RequestsUser,
    onViewProfile: () -> Unit,
    onViewFeedback: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
                Text(
                    "Жалоба на пользователя",
                    color = Color(0xFFFFB74D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFFFF9800).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "На модерации",
                        color = Color(0xFFFF9800),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "От кого: ${request.studentName ?: "Пользователь"}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewProfile() }
            )

            if (request.feedbackText != null) {
                Text(
                    "Отзыв: ${request.feedbackText}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onViewFeedback() }
                )
            } else {
                Text(
                    "Жалоба без отзыва",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Причина: ${request.descriptionRequest ?: "Нет описания"}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!request.dateRequest.isNullOrBlank()) {
                Text(
                    "Дата: ${request.dateRequest}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Одобрить", color = Color.White, fontSize = 12.sp)
                }

                
            }
        }
    }
}

@Composable
fun AdminRequestCompanyCard(
    request: RequestsCompany,
    onViewCompany: () -> Unit,
    onViewFeedback: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
                Text(
                    "Жалоба на компанию",
                    color = Color(0xFFFFB74D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFFFF9800).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "На модерации",
                        color = Color(0xFFFF9800),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "От компании: ${request.companyName ?: "Компания"}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewCompany() }
            )

            if (request.feedbackText != null) {
                Text(
                    "Отзыв: ${request.feedbackText}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onViewFeedback() }
                )
            } else {
                Text(
                    "Жалоба без отзыва",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Причина: ${request.descriptionRequset ?: "Нет описания"}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!request.dateRequest.isNullOrBlank()) {
                Text(
                    "Дата: ${request.dateRequest}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Одобрить", color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Отклонить", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
package org.example.project

import androidx.compose.foundation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.API.ApiClient
import org.example.project.Models.CurrentUser
import org.example.project.Models.Listresponcies
import org.example.project.Models.Student

@Composable
fun ResponsesScreen(
    api: ApiClient,
    type: String,
    onBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    var responses by remember { mutableStateOf<List<Listresponcies>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val title = when(type) {
        "consider" -> "Рассматриваемые"
        "invited" -> "Приглашения"
        "rejected" -> "Отклоненные"
        "favourites" -> "Избранные вакансии"
        else -> "Вакансии"
    }

    LaunchedEffect(Unit) {
        val user = Student(
            idStudent = CurrentUser.id ?: 0,
            emailStudent = CurrentUser.email ?: ""
        )

        val result = when(type) {
            "consider" -> api.getconsiderVacancy(user)
            "invited" -> api.getinvitedVacancy(user)
            "rejected" -> api.getrejectedVacancy(user)
            "favourites" -> api.getuserfavourities(user)
            else -> emptyList()
        }

        if (result != null) responses = result
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            } else if (responses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            if (type == "favourites") Icons.Default.FavoriteBorder
                            else Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (type == "favourites") "Нет избранных вакансий"
                            else "Нет вакансий",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(responses) { responseItem ->
                        ResponseDetailedCard(
                            item = responseItem,
                            onClick = {
                                onNavigateToDetail(responseItem.idVacancy ?: 0)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResponseDetailedCard(
    item: Listresponcies,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                item.nameVacancy ?: "Без названия",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${item.zenStart} - ${item.zenEnd} ${item.currency}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Длительность: ${item.time}",
                color = Color(0xFF5399BC),
                fontSize = 12.sp
            )

        }
    }
}
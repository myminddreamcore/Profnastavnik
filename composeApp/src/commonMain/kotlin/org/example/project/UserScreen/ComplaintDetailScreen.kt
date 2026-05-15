package org.example.project.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.ComplaintDTO

@Composable
fun ComplaintDetailScreen(
    api: ApiClient,
    complaintId: Int,
    onBack: () -> Unit,
    onNavigateToCompany: (Int) -> Unit,
    onNavigateToVacancy: (Int) -> Unit
) {
    var complaint by remember { mutableStateOf<ComplaintDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(complaintId) {
        val result = api.getComplaintById(complaintId)
        if (result != null) {
            complaint = result
        }
        isLoading = false
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
        } else if (complaint == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Жалоба не найдена", color = Color.White)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Детали жалобы", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Компания (кликабельно)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    complaint?.complaint?.idCompany?.let { onNavigateToCompany(it) }
                                }
                        ) {
                            Text("Компания:", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, modifier = Modifier.width(120.dp))
                            Text(
                                complaint?.nameCompany ?: "Не указано",
                                color = Color(0xFF5399BC),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, null, tint = Color(0xFF5399BC), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Вакансия (кликабельно)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    complaint?.complaint?.idVacancy?.let { onNavigateToVacancy(it) }
                                }
                        ) {
                            Text("Вакансия:", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, modifier = Modifier.width(120.dp))
                            Text(
                                complaint?.nameVacancy ?: "Не указано",
                                color = Color(0xFF5399BC),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, null, tint = Color(0xFF5399BC), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Статус
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Статус:", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, modifier = Modifier.width(120.dp))
                            Surface(
                                color = when (complaint?.complaint?.statusComplaint) {
                                    "Новая" -> Color(0xFFFF9800)
                                    "Рассмотрена" -> Color(0xFF4CAF50)
                                    else -> Color(0xFF9E9E9E)
                                }.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    complaint?.complaint?.statusComplaint ?: "Неизвестно",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = Color.White.copy(alpha = 0.2f))

                        Text("Описание жалобы:", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                        ) {
                            Text(
                                complaint?.complaint?.descriptionComplaint ?: "Нет описания",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.CardVacancy
import org.example.project.Models.CurrentUser
import org.example.project.Models.Listresponcies
import org.example.project.Models.Student
import org.example.project.Models.UserVacancies
@Composable
fun VacancyDetailScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var detail by remember { mutableStateOf<CardVacancy?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var isFav by remember { mutableStateOf(false) }
    var isProcessingFav by remember { mutableStateOf(false) }

    val userId = CurrentUser.id ?: 0

    LaunchedEffect(Unit) {
        val user = Student(idStudent = userId, emailStudent = CurrentUser.email ?: "")
        val result = api.getVacancy(user)

        if (result != null) {
            detail = result
            val favStatus = api.isFavourite(result.vacancy?.idVacancy ?: 0, userId)
            isFav = favStatus ?: false
        }
        isLoading = false
    }

    Scaffold(
        bottomBar = { CustomBottomNavigation("Дашборд") { onBack() } },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd))).padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            } else {
                detail?.let { data ->
                    val v = data.vacancy
                    val name = data.nameCompany
                    val currency = data.currency
                    val formats = data.formats?.joinToString(" ")

                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                        Spacer(modifier = Modifier.height(40.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                            IconButton(
                                onClick = {
                                    if (!isProcessingFav && v != null) {
                                        isProcessingFav = true
                                        scope.launch {
                                            val success = if (isFav) {
                                                api.deletefavourites(v.idVacancy, userId)
                                            } else {
                                                api.addfavourites(v.idVacancy, userId)
                                            }

                                            if (success == true) {
                                                isFav = !isFav
                                            }
                                            isProcessingFav = false
                                        }
                                    }
                                },
                                modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFav) Color.Red else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(60.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(v?.nameVacancy ?: "", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    InfoBadge("${v?.zenStartVacancy} - ${v?.zenEndVacancy} ${currency}")
                                    InfoBadge("${formats}")
                                    InfoBadge(v?.timeVacancy ?: "14 дней")
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(if(v?.anybodyVacancy == true) "С наставником" else "Без наставника", color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(v?.descriptionVacancy ?: "Описание отсутствует", color = Color.White.copy(alpha = 0.8f), lineHeight = 20.sp)
                                Spacer(modifier = Modifier.height(40.dp))
                                Text("${name}", color = Color.White, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Рейтинг ${data.ratingCompany}", color = Color.White, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Text("Читать отзывы", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBadge(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}
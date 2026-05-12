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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.CardVacancy
import org.example.project.Models.CurrentUser
import org.example.project.Models.Student

@Composable
fun VacancyDetailScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToCompany: (Int) -> Unit,
    vacancyId: Int = 0
) {
    var detail by remember { mutableStateOf<CardVacancy?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var isFav by remember { mutableStateOf(false) }
    var isProcessingFav by remember { mutableStateOf(false) }

    val userId = CurrentUser.id ?: 0

    LaunchedEffect(vacancyId) {
        if (vacancyId != 0) {
            val result = api.getVacancy(vacancyId)
            if (result != null) {
                detail = result
                val favStatus = api.isFavourite(vacancyId, userId)
                isFav = favStatus ?: false
            }
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
        } else {
            detail?.let { data ->
                val v = data.vacancy
                val name = data.nameCompany
                val companyId = data.companyId
                val currency = data.currency
                val formats = data.formats?.joinToString(",\n")
                val skills = data.skills?.joinToString(", ") ?: ""
                val sferes = data.sferes?.joinToString(", ") ?: ""

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
                            modifier = Modifier.background(
                                Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Избранное",
                                tint = if (isFav) Color.Red else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                v?.nameVacancy ?: "",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            if ((v?.zenStartVacancy != null && v?.zenEndVacancy != null) ||
                                formats?.isNotBlank() == true ||
                                v?.timeVacancy != null) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (v?.zenStartVacancy != null && v?.zenEndVacancy != null) {
                                        InfoBadge("${v.zenStartVacancy} \n- ${v.zenEndVacancy} $currency")
                                    }
                                    if (formats?.isNotBlank() == true) {
                                        InfoBadge(formats)
                                    }
                                    if (v?.timeVacancy != null) {
                                        InfoBadge(v.timeVacancy!!)
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            if (v?.anybodyVacancy != null) {
                                Text(
                                    if (v.anybodyVacancy == true) "С наставником" else "Без наставника",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (!v?.descriptionVacancy.isNullOrBlank()) {
                                Text(
                                    v?.descriptionVacancy ?: "",
                                    color = Color.White.copy(alpha = 0.8f),
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            if (skills.isNotBlank()) {
                                Text(
                                    "Навыки:",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    skills,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (sferes.isNotBlank()) {
                                Text(
                                    "Сферы деятельности:",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    sferes,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (!name.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            companyId?.let { onNavigateToCompany(it) }
                                        }
                                ) {
                                    Text(
                                        name,
                                        color = Color(0xFF5399BC),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.OpenInNew,
                                        null,
                                        tint = Color(0xFF5399BC),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (data.ratingCompany != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Рейтинг ${data.ratingCompany}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Star,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text(
                                "Читать отзывы",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { /* TODO: Переход к отзывам */ }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun InfoBadge(text: String) {
    Box(
        modifier = Modifier
            .background(
                Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
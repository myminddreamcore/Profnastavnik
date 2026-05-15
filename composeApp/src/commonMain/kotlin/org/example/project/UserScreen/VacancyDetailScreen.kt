package org.example.project.UserScreen

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CardVacancy
import org.example.project.Models.CurrentUser
import org.example.project.Models.Response
import org.example.project.Models.FeedbacksUser
import org.example.project.Models.UserPrice

@Composable
fun VacancyDetailScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToCompany: (Int) -> Unit,
    onNavigateToTariffs: () -> Unit,
    vacancyId: Int = 0
) {
    var detail by remember { mutableStateOf<CardVacancy?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showResponseDialog by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var coverLetter by remember { mutableStateOf("") }
    var portfolioLink by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var reviews by remember { mutableStateOf<List<FeedbacksUser>>(emptyList()) }
    var showReviews by remember { mutableStateOf(false) }
    var userPrice by remember { mutableStateOf<UserPrice?>(null) }
    var responseCount by remember { mutableStateOf(0) }
    var isLoadingPrice by remember { mutableStateOf(true) }
    var showLimitDialog by remember { mutableStateOf(false) }

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
                val companyId = result.companyId
                if (companyId != null) {
                    val reviewsResult = api.getCompanyFeedback(companyId)
                    if (reviewsResult != null) {
                        reviews = reviewsResult
                    }
                }
            }
        }

        val price = api.getUserPrice(userId)
        userPrice = price
        if (price?.idPrices == 1) {
            val count = api.getUserResponseCount(userId)
            responseCount = count ?: 0
        }
        isLoadingPrice = false
        isLoading = false
    }

    fun checkCanRespond(): Boolean {
        val price = userPrice
        return if (price?.idPrices == 1) {
            responseCount < 5
        } else {
            true
        }
    }

    fun sendResponse() {
        if (coverLetter.isBlank()) {
            errorMessage = "Введите сопроводительное письмо"
            return
        }

        if (!checkCanRespond()) {
            showLimitDialog = true
            return
        }

        scope.launch {
            isSending = true
            errorMessage = null

            val response = Response(
                idResponse = 0,
                idCompany = detail?.companyId,
                idUser = userId,
                descriptionResponse = coverLetter,
                portfolioResponse = portfolioLink.takeIf { it.isNotBlank() },
                statusResponse = "Рассматривается",
                idVacancy = vacancyId
            )

            val result = api.addResponse(response)
            isSending = false

            if (result != null) {
                successMessage = "Отклик успешно отправлен!"
                showResponseDialog = false
                coverLetter = ""
                portfolioLink = ""
                if (userPrice?.idPrices == 1) {
                    val newCount = api.getUserResponseCount(userId)
                    responseCount = newCount ?: responseCount + 1
                }
            } else {
                errorMessage = "Вы уже откликались на эту вакансию"
            }
        }
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))

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
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    item {
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
                            }
                        }
                    }

                    if (reviews.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Отзывы (${reviews.size})",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(
                                    onClick = { showReviews = !showReviews },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF5399BC))
                                ) {
                                    Text(if (showReviews) "Свернуть" else "Развернуть")
                                }
                            }
                        }

                        if (showReviews) {
                            items(reviews) { review ->
                                ReviewCard(review = review)
                            }
                        } else {
                            item {
                                val firstReview = reviews.firstOrNull()
                                if (firstReview != null) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showReviews = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                repeat(5) { index ->
                                                    Icon(
                                                        if (index < (firstReview.ratingFeedbackUser ?: 0))
                                                            Icons.Default.Star
                                                        else
                                                            Icons.Default.StarBorder,
                                                        null,
                                                        tint = if (index < (firstReview.ratingFeedbackUser ?: 0))
                                                            Color(0xFFFFB74D)
                                                        else
                                                            Color.White.copy(alpha = 0.3f),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "${firstReview.ratingFeedbackUser}/5",
                                                    color = Color.White.copy(alpha = 0.5f),
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                firstReview.descriptionFeedbackUser ?: "Нет описания",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 12.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "Читать все ${reviews.size} отзывов...",
                                                color = Color(0xFF5399BC),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        if (userPrice?.idPrices == 1 && !isLoadingPrice) {
                            val remaining = 5 - responseCount
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (remaining > 0) Icons.Default.Info else Icons.Default.Warning,
                                        null,
                                        tint = if (remaining > 0) Color(0xFF5399BC) else Color(0xFFFF9800),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (remaining > 0) "Осталось откликов в этом месяце: $remaining из 5"
                                        else "Лимит откликов исчерпан. Перейдите на тариф Профи",
                                        color = if (remaining > 0) Color.White.copy(alpha = 0.8f) else Color(0xFFFF9800),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        GradientResponseButton(
                            text = if (userPrice?.idPrices == 1 && responseCount >= 5) "Откликнуться (лимит исчерпан)" else "Откликнуться",
                            icon = Icons.Default.Send,
                            onClick = {
                                if (userPrice?.idPrices == 1 && responseCount >= 5) {
                                    showLimitDialog = true
                                } else {
                                    showResponseDialog = true
                                }
                            },
                            gradient = if (userPrice?.idPrices == 1 && responseCount >= 5)
                                listOf(Color(0xFF9E9E9E), Color(0xFF757575))
                            else
                                listOf(Color(0xFF4A90E2), Color(0xFF7B61FF))
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    if (showResponseDialog) {
        AlertDialog(
            onDismissRequest = {
                showResponseDialog = false
                errorMessage = null
            },
            title = {
                Text(
                    "Сопроводительное письмо",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            containerColor = Color(0xFF1E1E2E),
            modifier = Modifier.fillMaxWidth(0.95f),
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        "Расскажите почему вы подходите на эту позицию",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = coverLetter,
                        onValueChange = {
                            coverLetter = it
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        placeholder = {
                            Text(
                                "Напишите сопроводительное письмо...",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF5399BC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Ссылка на портфолио (необязательно)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = portfolioLink,
                        onValueChange = { portfolioLink = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "https://github.com/ваш-профиль",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF5399BC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMessage!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    if (successMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            successMessage!!,
                            color = Color.Green,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { sendResponse() },
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC))
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Отправка...", color = Color.White)
                    } else {
                        Text("Отправить отклик", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showResponseDialog = false
                        errorMessage = null
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Отмена", color = Color.White)
                }
            }
        )
    }

    if (showLimitDialog) {
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            title = {
                Text(
                    "Лимит откликов исчерпан",
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "У вас закончились бесплатные отклики на этом месяце.",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Перейдите на тариф Профи, чтобы получать неограниченное количество откликов!",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                TextButton(
                    onClick = {
                        showLimitDialog = false
                        onNavigateToTariffs()
                    }
                ) {
                    Text("Перейти к тарифам", color = Color(0xFF5399BC))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text("Отмена", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}

@Composable
fun ReviewCard(review: FeedbacksUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        if (index < (review.ratingFeedbackUser ?: 0))
                            Icons.Default.Star
                        else
                            Icons.Default.StarBorder,
                        null,
                        tint = if (index < (review.ratingFeedbackUser ?: 0))
                            Color(0xFFFFB74D)
                        else
                            Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${review.ratingFeedbackUser}/5",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                review.descriptionFeedbackUser ?: "Нет описания",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
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

@Composable
fun GradientResponseButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradient),
                    shape = RoundedCornerShape(28.dp)
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
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
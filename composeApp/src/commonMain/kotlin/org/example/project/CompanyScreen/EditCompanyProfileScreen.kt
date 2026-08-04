package org.example.project.CompanyScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CurrentUser
import org.example.project.Models.Director
import org.example.project.Models.FeedbacksUser
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun EditCompanyProfileScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onSave: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var director by remember { mutableStateOf<Director?>(null) }
    var companyRating by remember { mutableStateOf(0.0) }
    var reviews by remember { mutableStateOf<List<FeedbacksUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showReviews by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    var nameCompany by remember { mutableStateOf("") }
    var descriptionDirector by remember { mutableStateOf("") }
    var websiteDirector by remember { mutableStateOf("") }
    var phoneDirector by remember { mutableStateOf("") }
    var cityDirector by remember { mutableStateOf("") }
    var nameDirector by remember { mutableStateOf("") }
    var surnameDirector by remember { mutableStateOf("") }
    var patronymicDirector by remember { mutableStateOf("") }
    var emailDirector by remember { mutableStateOf("") }
    var statusDirector by remember { mutableStateOf("") }
    var dateCreatedDirector by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val companyId = CurrentUser.id ?: 0

            val companyData = api.getCompanyCard(companyId)
            if (companyData != null) {
                director = companyData.director
                companyRating = companyData.rating ?: 0.0

                director?.let { d ->
                    nameCompany = d.nameCompanyDirector ?: ""
                    descriptionDirector = d.descriptionDirector ?: ""
                    websiteDirector = d.websiteDirector ?: ""
                    phoneDirector = d.phoneDirector ?: ""
                    cityDirector = d.cityDirector ?: ""
                    nameDirector = d.nameDirector ?: ""
                    surnameDirector = d.surnameDirector ?: ""
                    patronymicDirector = d.patronymicDirector ?: ""
                    emailDirector = d.emailDirector ?: ""
                    statusDirector = d.statusDirector ?: ""
                    dateCreatedDirector = d.dateCreatedDirector ?: ""
                }

                val reviewsResult = api.getCompanyFeedback(companyId)
                if (reviewsResult != null) {
                    reviews = reviewsResult
                }
            } else {
                errorMessage = "Не удалось загрузить данные компании"
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
        isLoading = false
    }

    val snackbarHostState = remember { SnackbarHostState() }

    fun saveProfile() {
        scope.launch {
            if (isSaving) return@launch
            isSaving = true
            try {
                val companyId = CurrentUser.id ?: 0

                val updatedDirector = Director(
                    idDirector = companyId,
                    emailDirector = emailDirector,
                    nameDirector = nameDirector,
                    surnameDirector = surnameDirector,
                    patronymicDirector = patronymicDirector,
                    nameCompanyDirector = nameCompany,
                    descriptionDirector = descriptionDirector,
                    websiteDirector = websiteDirector,
                    phoneDirector = phoneDirector,
                    statusDirector = statusDirector,
                    dateCreatedDirector = dateCreatedDirector,
                    timeCreatedDirector = director?.timeCreatedDirector,
                    cityDirector = cityDirector,
                    authenticationDirector = director?.authenticationDirector
                )

                val success = api.saveCompanyProfile(updatedDirector)

                if (success) {
                    snackbarHostState.showSnackbar("Данные компании сохранены!")
                    onSave()
                } else {
                    snackbarHostState.showSnackbar("Ошибка сохранения")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Ошибка: ${e.message}")
            }
            isSaving = false
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Профиль") { target ->
                onNavigate(target)
            }
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Ошибка", color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Редактирование профиля компании", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Icon(Icons.Default.Business, null, tint = Color.White, modifier = Modifier.fillMaxSize().padding(20.dp))
                        }
                        IconButton(
                            onClick = { /* TODO: Загрузка фото */ },
                            modifier = Modifier.background(Color.White, CircleShape).size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Все поля с правильным синтаксисом
                    CustomInputField("Название компании", nameCompany, enabled = true) { nameCompany = it }
                    CustomInputField("Email", emailDirector, enabled = false)
                    CustomInputField("Телефон", phoneDirector, enabled = true) { phoneDirector = it }
                    CustomInputField("Сайт", websiteDirector, enabled = true) { websiteDirector = it }
                    CustomInputField("Город", cityDirector, enabled = true) { cityDirector = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Руководитель", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomInputField("Фамилия", surnameDirector, enabled = true) { surnameDirector = it }
                    CustomInputField("Имя", nameDirector, enabled = true) { nameDirector = it }
                    CustomInputField("Отчество", patronymicDirector, enabled = true) { patronymicDirector = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("О компании", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomInputField("Описание", descriptionDirector, enabled = true) { descriptionDirector = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Рейтинг
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Рейтинг:", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${(companyRating * 10).toInt() / 10.0}",
                            color = Color(0xFFFFB74D),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB74D), modifier = Modifier.size(20.dp))
                    }

                    // Статус
                    if (statusDirector.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Статус:", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = when (statusDirector) {
                                    "Активен" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    statusDirector,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Дата регистрации
                    if (dateCreatedDirector.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Зарегистрирована: $dateCreatedDirector",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (director?.authenticationDirector == true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Компания подтверждена", color = Color(0xFF4CAF50), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

// Отзывы о компании - как в карточке компании
                    if (reviews.isNotEmpty()) {
                        Text(
                            "Отзывы о компании (${reviews.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Превью первого отзыва
                        val firstReview = reviews.firstOrNull()
                        if (firstReview != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showReviews = !showReviews },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(5) { index ->
                                            Icon(
                                                if (index < (firstReview.ratingFeedbackUser ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                                                null,
                                                tint = if (index < (firstReview.ratingFeedbackUser ?: 0)) Color(0xFFFFB74D) else Color.White.copy(alpha = 0.3f),
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

                        // Все отзывы (развернуто)
                        if (showReviews) {
                            Spacer(modifier = Modifier.height(8.dp))
                            reviews.forEach { review ->
                                ReviewItem(review = review)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        text = "Сохранить изменения",
                        icon = Icons.Default.Save,
                        onClick = { saveProfile() },
                        gradient = listOf(Color(0xFF5399BC), Color(0xFF7B61FF))
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledTextColor = Color.White.copy(alpha = 0.5f),
                disabledBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun ReviewItem(review: FeedbacksUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        if (index < (review.ratingFeedbackUser ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                        null,
                        tint = if (index < (review.ratingFeedbackUser ?: 0)) Color(0xFFFFB74D) else Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("${review.ratingFeedbackUser}/5", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                review.descriptionFeedbackUser ?: "",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    gradient: List<Color>
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        content = {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.horizontalGradient(gradient),
                    shape = RoundedCornerShape(16.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
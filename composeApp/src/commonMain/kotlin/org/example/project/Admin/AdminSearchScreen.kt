package org.example.project.Admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
fun AdminSearchScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToCompanyProfile: (Int) -> Unit,
    onNavigateToVacancyDetail: (Int) -> Unit,
    onNavigateToFeedbackDetail: (Int) -> Unit,
    onNavigateToChat: (Int, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchType by remember { mutableStateOf("all") }
    var searchResults by remember { mutableStateOf<List<AdminSearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val searchTypes = listOf(
        "all" to "Все",
        "vacancy" to "Вакансии",
        "user" to "Пользователи",
        "company" to "Компании",
        "feedback" to "Отзывы"
    )

    fun performSearch() {
        scope.launch {
            isLoading = true
            hasSearched = true

            val filters = AdminSearchFilters(
                query = searchQuery.takeIf { it.isNotBlank() },
                type = if (searchType == "all") null else searchType
            )

            val results = api.adminSearch(filters)
            searchResults = results ?: emptyList()
            isLoading = false
        }
    }

    fun updateStatus(result: AdminSearchResult, newStatus: String) {
        scope.launch {
            val success = when (result.type) {
                "user" -> api.updateUserStatus(result.id, newStatus)
                "company" -> api.updateCompanyStatus(result.id, newStatus)
                "vacancy" -> api.updateVacancyStatus(result.id, newStatus)
                "feedback" -> {
                    if (result.title.contains("студенте")) {
                        api.updateFeedbackCompanyStatus(result.id, newStatus)
                    } else {
                        api.updateFeedbackUserStatus(result.id, newStatus)
                    }
                }
                else -> false
            }

            if (success) {
                val updatedResults = searchResults.map { item ->
                    if (item.id == result.id && item.type == result.type) {
                        item.copy(status = newStatus)
                    } else {
                        item
                    }
                }
                searchResults = updatedResults
                snackbarHostState.showSnackbar("Статус обновлен: $newStatus")
            } else {
                snackbarHostState.showSnackbar("Ошибка обновления статуса")
            }
        }
    }

    fun handleResultClick(result: AdminSearchResult) {
        when (result.type) {
            "user" -> onNavigateToUserProfile(result.id)
            "company" -> onNavigateToCompanyProfile(result.id)
            "vacancy" -> onNavigateToVacancyDetail(result.id)
            "feedback" -> onNavigateToFeedbackDetail(result.id)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Поиск") { target ->
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Админ-поиск",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(modifier = Modifier.size(40.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        placeholder = { Text("Поиск по ID или тексту...", color = Color.White.copy(alpha = 0.6f)) },
                        shape = RoundedCornerShape(27.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(onClick = { performSearch() }) {
                                Icon(Icons.Default.Search, null, tint = Color.White)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        "Тип поиска:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        searchTypes.forEach { (value, label) ->
                            FilterChip(
                                selected = searchType == value,
                                onClick = { searchType = value },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF5399BC),
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    selectedLabelColor = Color.White,
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            when {
                                isLoading -> "Поиск..."
                                !hasSearched -> "Введите запрос для поиска"
                                else -> "Найдено: ${searchResults.size}"
                            },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when {
                            isLoading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = Color.White)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Поиск...", color = Color.White.copy(alpha = 0.6f))
                                    }
                                }
                            }
                            !hasSearched -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Search,
                                            null,
                                            tint = Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Введите запрос для поиска", color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                            searchResults.isEmpty() -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.SearchOff,
                                            null,
                                            tint = Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Ничего не найдено", color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                            else -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(searchResults) { result ->
                                        AdminSearchResultCard(
                                            result = result,
                                            api = api,
                                            onClick = { handleResultClick(result) },
                                            onUpdateStatus = { newStatus ->
                                                updateStatus(result, newStatus)
                                            },
                                            onSendMessage = { id, type ->
                                                onNavigateToChat(id, type)
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
fun AdminSearchResultCard(
    result: AdminSearchResult,
    api: ApiClient,
    onClick: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onSendMessage: (Int, String) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Состояния для тарифа
    var userTariffName by remember { mutableStateOf<String?>(null) }
    var companyTariffName by remember { mutableStateOf<String?>(null) }
    var userTariffId by remember { mutableStateOf<Int?>(null) }
    var companyTariffId by remember { mutableStateOf<Int?>(null) }
    var showTariffDialog by remember { mutableStateOf(false) }
    var allUserTariffs by remember { mutableStateOf<List<PricesForUser>>(emptyList()) }
    var allCompanyTariffs by remember { mutableStateOf<List<PricesForCompany>>(emptyList()) }
    var isLoadingTariff by remember { mutableStateOf(false) }

    // Загружаем тариф
    LaunchedEffect(result.id, result.type) {
        if (result.type == "user") {
            isLoadingTariff = true
            val userPrice = api.getUserPrice(result.id)
            val allPrices = api.getAllPrices()

            if (allPrices != null) {
                allUserTariffs = allPrices
                if (userPrice != null) {
                    userTariffId = userPrice.idPrices
                    val currentPrice = allPrices.find { it.idPrice == userPrice.idPrices }
                    userTariffName = currentPrice?.namePrice ?: "Базовый"
                } else {
                    userTariffId = 1
                    userTariffName = allPrices.find { it.idPrice == 1 }?.namePrice ?: "Базовый"
                }
            }
            isLoadingTariff = false
        } else if (result.type == "company") {
            isLoadingTariff = true
            val companyPrice = api.getCompanyTariff(result.id)
            val allPrices = api.getAllCompanyTariffs()

            if (allPrices != null) {
                allCompanyTariffs = allPrices
                if (companyPrice != null) {
                    companyTariffId = companyPrice.idPrice
                    val currentPrice = allPrices.find { it.id == companyPrice.idPrice }
                    companyTariffName = currentPrice?.namePrice ?: "Базовый"
                } else {
                    companyTariffId = 1
                    companyTariffName = allPrices.find { it.id == 1 }?.namePrice ?: "Базовый"
                }
            }
            isLoadingTariff = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = when (result.type) {
                        "user" -> Color(0xFF7B61FF).copy(alpha = 0.3f)
                        "company" -> Color(0xFF5399BC).copy(alpha = 0.3f)
                        "vacancy" -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                        "feedback" -> Color(0xFFFFB74D).copy(alpha = 0.3f)
                        else -> Color.White.copy(alpha = 0.1f)
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            when (result.type) {
                                "user" -> Icons.Default.Person
                                "company" -> Icons.Default.Business
                                "vacancy" -> Icons.Default.Work
                                "feedback" -> Icons.Default.Comment
                                else -> Icons.Default.Search
                            },
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        result.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!result.subtitle.isNullOrBlank()) {
                        Text(
                            result.subtitle,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // ========== ТАРИФ ==========
                    if (result.type == "user" || result.type == "company") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(14.dp)
                            )
                            if (isLoadingTariff) {
                                Text(
                                    "Загрузка...",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    "Тариф: ${if (result.type == "user") userTariffName ?: "..." else companyTariffName ?: "..."}",
                                    color = Color(0xFFFFB74D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Статус
                    if (!result.status.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Статус:",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                            Surface(
                                color = when (result.status) {
                                    "Активен", "Активна", "Одобрен", "Одобрено" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    "Удален", "Архив", "Удалено" -> Color(0xFFB71C1C).copy(alpha = 0.2f)
                                    else -> Color.White.copy(alpha = 0.1f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    result.status,
                                    color = when (result.status) {
                                        "Активен", "Активна", "Одобрен", "Одобрено" -> Color(0xFF4CAF50)
                                        "Удален", "Архив", "Удалено" -> Color(0xFFB71C1C)
                                        else -> Color.White.copy(alpha = 0.7f)
                                    },
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Тег типа
                Surface(
                    color = when (result.type) {
                        "user" -> Color(0xFF7B61FF).copy(alpha = 0.2f)
                        "company" -> Color(0xFF5399BC).copy(alpha = 0.2f)
                        "vacancy" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "feedback" -> Color(0xFFFFB74D).copy(alpha = 0.2f)
                        else -> Color.White.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        when (result.type) {
                            "user" -> "Студент"
                            "company" -> "Компания"
                            "vacancy" -> "Вакансия"
                            "feedback" -> "Отзыв"
                            else -> "Другое"
                        },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ========== КНОПКИ ==========
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (result.type) {
                    "user" -> {
                        StatusButton(
                            label = "Тариф",
                            color = Color(0xFFFFB74D),
                            icon = Icons.Default.CreditCard,
                            onClick = { showTariffDialog = true }
                        )
                        StatusButton(
                            label = "Написать",
                            color = Color(0xFF5399BC),
                            icon = Icons.Default.Chat,
                            onClick = { onSendMessage(result.id, "user") }
                        )
                        if (result.status != "Активен") {
                            StatusButton(
                                label = "Активен",
                                color = Color(0xFF4CAF50),
                                onClick = { onUpdateStatus("Активен") }
                            )
                        }
                        if (result.status != "Удален") {
                            StatusButton(
                                label = "Удалить",
                                color = Color(0xFFB71C1C),
                                onClick = { onUpdateStatus("Удален") }
                            )
                        }
                    }
                    "company" -> {
                        StatusButton(
                            label = "Тариф",
                            color = Color(0xFFFFB74D),
                            icon = Icons.Default.CreditCard,
                            onClick = { showTariffDialog = true }
                        )
                        StatusButton(
                            label = "Написать",
                            color = Color(0xFF5399BC),
                            icon = Icons.Default.Chat,
                            onClick = { onSendMessage(result.id, "company") }
                        )
                        if (result.status != "Активен") {
                            StatusButton(
                                label = "Активен",
                                color = Color(0xFF4CAF50),
                                onClick = { onUpdateStatus("Активен") }
                            )
                        }
                        if (result.status != "Удален") {
                            StatusButton(
                                label = "Удален",
                                color = Color(0xFFB71C1C),
                                onClick = { onUpdateStatus("Удален") }
                            )
                        }
                    }
                    "vacancy" -> {
                        if (result.status != "Активна") {
                            StatusButton(
                                label = "Активна",
                                color = Color(0xFF4CAF50),
                                onClick = { onUpdateStatus("Активна") }
                            )
                        }
                        if (result.status != "Архив") {
                            StatusButton(
                                label = "В Архив",
                                color = Color(0xFFB71C1C),
                                onClick = { onUpdateStatus("Архив") }
                            )
                        }
                    }
                    "feedback" -> {
                        val isAboutStudent = result.title.contains("студенте")
                        val approvedLabel = if (isAboutStudent) "Одобрено" else "Одобрен"
                        val deletedLabel = if (isAboutStudent) "Удалено" else "Удален"

                        if (result.status != approvedLabel) {
                            StatusButton(
                                label = approvedLabel,
                                color = Color(0xFF4CAF50),
                                onClick = { onUpdateStatus(approvedLabel) }
                            )
                        }
                        if (result.status != deletedLabel) {
                            StatusButton(
                                label = deletedLabel,
                                color = Color(0xFFB71C1C),
                                onClick = { onUpdateStatus(deletedLabel) }
                            )
                        }
                    }
                }
            }
        }
    }

    // ========== ДИАЛОГ СМЕНЫ ТАРИФА ==========
    if (showTariffDialog) {
        AlertDialog(
            onDismissRequest = { showTariffDialog = false },
            title = {
                Text(
                    "Смена тарифа",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Текущий тариф: ${if (result.type == "user") userTariffName ?: "..." else companyTariffName ?: "..."}",
                        color = Color(0xFFFFB74D),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Выберите новый тариф:",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (result.type == "user") {
                        allUserTariffs.forEach { tariff ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        scope.launch {
                                            val newPrice = UserPrice(
                                                id = 0,
                                                idUser = result.id,
                                                idPrices = tariff.idPrice
                                            )
                                            val success = api.addNewUserPrice(newPrice)
                                            if (success != null) {
                                                userTariffName = tariff.namePrice
                                                userTariffId = tariff.idPrice
                                                showTariffDialog = false
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (userTariffId == tariff.idPrice) {
                                        Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    } else {
                                        Color.White.copy(alpha = 0.1f)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            tariff.namePrice ?: "Тариф",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${tariff.costPrice ?: 0} ₽",
                                            color = Color(0xFFFFB74D),
                                            fontSize = 12.sp
                                        )
                                    }
                                    if (userTariffId == tariff.idPrice) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            tint = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        allCompanyTariffs.forEach { tariff ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        scope.launch {
                                            val newPrice = CompanyPrice(
                                                id = 0,
                                                idCompany = result.id,
                                                idPrice = tariff.id
                                            )
                                            val success = api.addNewCompanyPrice(newPrice)
                                            if (success != null) {
                                                companyTariffName = tariff.namePrice
                                                companyTariffId = tariff.id
                                                showTariffDialog = false
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (companyTariffId == tariff.id) {
                                        Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    } else {
                                        Color.White.copy(alpha = 0.1f)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            tariff.namePrice ?: "Тариф",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${tariff.cost ?: 0} ₽",
                                            color = Color(0xFFFFB74D),
                                            fontSize = 12.sp
                                        )
                                    }
                                    if (companyTariffId == tariff.id) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            tint = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTariffDialog = false }) {
                    Text("Закрыть", color = Color(0xFF5399BC))
                }
            },
            containerColor = Color(0xFF1E1E2E)
        )
    }
}

@Composable
fun StatusButton(
    label: String,
    color: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
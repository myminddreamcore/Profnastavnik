package org.example.project.CompanyScreen

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.API.MultiSelectFieldFromApi
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVacancyScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onNavigateToTariffs: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Справочники
    var moneyTypes by remember { mutableStateOf<List<MoneyType>>(emptyList()) }
    var currencies by remember { mutableStateOf<List<Currency>>(emptyList()) }
    var allSkills by remember { mutableStateOf<List<Skills>>(emptyList()) }
    var allSferes by remember { mutableStateOf<List<Sferes>>(emptyList()) }
    var allProfessions by remember { mutableStateOf<List<Proffesions>>(emptyList()) }
    var allFormats by remember { mutableStateOf<List<Formats>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }

    // Основные поля
    var nameVacancy by remember { mutableStateOf("") }
    var descriptionVacancy by remember { mutableStateOf("") }
    var timeVacancy by remember { mutableStateOf("") }
    var countUserVacancy by remember { mutableStateOf("") }
    var zenStartVacancy by remember { mutableStateOf("") }
    var zenEndVacancy by remember { mutableStateOf("") }
    var hasMentor by remember { mutableStateOf(false) }
    var selectedMoneyType by remember { mutableStateOf<MoneyType?>(null) }
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }

    // Мультивыбор с автодополнением
    var selectedSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSferes by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedProfessions by remember { mutableStateOf<List<String>>(emptyList()) }

    // Одиночный выбор формата
    var selectedFormat by remember { mutableStateOf<String?>(null) }

    // Адрес
    var cityAdress by remember { mutableStateOf("") }
    var streetAdress by remember { mutableStateOf("") }
    var houseAdress by remember { mutableStateOf("") }
    var flatAdress by remember { mutableStateOf("") }
    var indexAdress by remember { mutableStateOf("") }

    // Dropdown состояния
    var expandedMoney by remember { mutableStateOf(false) }
    var expandedCurrency by remember { mutableStateOf(false) }
    var expandedFormat by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    var companyPrice by remember { mutableStateOf<CompanyPrice?>(null) }
    var existingVacancies by remember { mutableStateOf<List<Listresponcies>>(emptyList()) }
    var isLoadingTariff by remember { mutableStateOf(true) }
    var showTariffLimitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            delay(1500)
            onSuccess()
        }
    }

    LaunchedEffect(Unit) {
        isLoadingData = true
        isLoadingTariff = true

        val companyId = CurrentUser.id ?: 0

        // Загружаем справочники
        val money = api.getMoneyTypes()
        if (money != null) moneyTypes = money

        val currency = api.getCurrencies()
        if (currency != null) currencies = currency

        val skills = api.getSkills()
        if (skills != null) allSkills = skills

        val sferes = api.getSferes()
        if (sferes != null) allSferes = sferes

        val professions = api.getProffesions()
        if (professions != null) allProfessions = professions

        val formats = api.getFormats()
        if (formats != null) allFormats = formats

        // Тариф
        val tariff = api.getCompanyTariff(companyId)
        companyPrice = tariff

        // Существующие вакансии
        val director = Director(idDirector = companyId)
        val vacancies = api.getCompanyVacancies(director)
        if (vacancies != null) {
            existingVacancies = vacancies
        }

        isLoadingData = false
        isLoadingTariff = false
    }

    // Списки названий для автодополнения
    val skillNames = allSkills.map { it.nameSkill ?: "" }
    val sfereNames = allSferes.map { it.nameSfere ?: "" }
    val professionNames = allProfessions.map { it.nameProfession ?: "" }
    val formatNames = allFormats.map { it.nameVacancyFormat ?: "" }

    fun canCreateVacancy(): Boolean {
        val tariffId = companyPrice?.idPrice ?: 1
        val currentCount = existingVacancies.size

        return when (tariffId) {
            1 -> currentCount < 1
            2 -> currentCount < 5
            3 -> true
            else -> false
        }
    }

    fun getVacancyLimit(): Int {
        val tariffId = companyPrice?.idPrice ?: 1
        return when (tariffId) {
            1 -> 1
            2 -> 5
            3 -> Int.MAX_VALUE
            else -> 0
        }
    }

    fun createVacancy() {
        if (!canCreateVacancy()) {
            showTariffLimitDialog = true
            return
        }

        if (nameVacancy.isBlank()) {
            errorMessage = "Введите название вакансии"
            return
        }
        if (descriptionVacancy.isBlank()) {
            errorMessage = "Введите описание вакансии"
            return
        }
        if (selectedMoneyType == null) {
            errorMessage = "Выберите тип оплаты"
            return
        }


        scope.launch {
            isLoading = true
            errorMessage = null

            val vacancy = Vacancy(
                idVacancy = 0,
                idCompany = CurrentUser.id ?: 0,
                nameVacancy = nameVacancy,
                descriptionVacancy = descriptionVacancy,
                timeVacancy = timeVacancy,
                moneyTypeVacancy = selectedMoneyType?.idMoneyType,
                anybodyVacancy = hasMentor,
                statusVacancy = "На модерации",
                countUserVacancy = countUserVacancy.toIntOrNull(),
                zenStartVacancy = zenStartVacancy.toIntOrNull(),
                zenEndVacancy = zenEndVacancy.toIntOrNull(),
                currencyVacancy = selectedCurrency?.idCurrency
            )

            val adress = Adress(
                idAdress = 0,
                cityAdress = cityAdress,
                streetAdress = streetAdress,
                houseAdress = houseAdress,
                flatAdress = flatAdress,
                indexAdress = indexAdress.toIntOrNull(),
                idVacancy = 0
            )

            val dto = CreateVacancyDTO(
                vacancy = vacancy,
                adress = adress,
                skills = if (selectedSkills.isNotEmpty()) selectedSkills else null,
                sferes = if (selectedSferes.isNotEmpty()) selectedSferes else null,

                format = selectedFormat
            )

            val success = api.createVacancy(dto)
            isLoading = false

            if (success) {
                successMessage = "Вакансия успешно создана и отправлена на модерацию!"
                showSuccess = true
            } else {
                errorMessage = "Вакансия с таким названием уже существует"
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Создать вакансию", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5399BC).copy(alpha = 0.9f)
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            if (isLoadingData || isLoadingTariff) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Загрузка...", color = Color.White)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Информация о тарифе
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF5399BC).copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Тариф: ${getTariffName(companyPrice?.idPrice ?: 1)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Вакансий: ${existingVacancies.size} из ${if (getVacancyLimit() == Int.MAX_VALUE) "∞" else getVacancyLimit()}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                            if (!canCreateVacancy()) {
                                Button(
                                    onClick = { onNavigateToTariffs() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF9800)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Повысить", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Скролл
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        OutlinedTextField(
                            value = nameVacancy,
                            onValueChange = { nameVacancy = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Название вакансии", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = descriptionVacancy,
                            onValueChange = { descriptionVacancy = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            label = { Text("Описание", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = timeVacancy,
                            onValueChange = { timeVacancy = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Длительность", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = countUserVacancy,
                            onValueChange = { countUserVacancy = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Количество стажеров", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== НАВЫКИ (АВТОДОПОЛНЕНИЕ) ==========
                        MultiSelectFieldFromApi(
                            label = "Навыки",
                            options = skillNames,
                            selectedOptionsNames = selectedSkills,
                            onOptionsSelected = { newList -> selectedSkills = newList },
                            placeholder = "Введите навык..."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== СФЕРЫ (АВТОДОПОЛНЕНИЕ) ==========
                        MultiSelectFieldFromApi(
                            label = "Сферы деятельности",
                            options = sfereNames,
                            selectedOptionsNames = selectedSferes,
                            onOptionsSelected = { newList -> selectedSferes = newList },
                            placeholder = "Введите сферу..."
                        )

                        Spacer(modifier = Modifier.height(16.dp))



                        Spacer(modifier = Modifier.height(16.dp))

                        // ========== ФОРМАТ (ОДИНОЧНЫЙ ВЫБОР) ==========
                        Box {
                            OutlinedTextField(
                                value = selectedFormat ?: "",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedFormat = !expandedFormat },
                                readOnly = true,
                                label = { Text("Формат работы", color = Color.White.copy(alpha = 0.7f)) },
                                trailingIcon = {
                                    IconButton(onClick = { expandedFormat = !expandedFormat }) {
                                        Icon(
                                            if (expandedFormat) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                colors = textFieldColors()
                            )

                            DropdownMenu(
                                expanded = expandedFormat,
                                onDismissRequest = { expandedFormat = false },
                                modifier = Modifier.background(Color(0xFF2D3243))
                            ) {
                                formatNames.forEach { format ->
                                    DropdownMenuItem(
                                        text = { Text(format, color = Color.White) },
                                        onClick = {
                                            selectedFormat = format
                                            expandedFormat = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Тип оплаты
                        Box {
                            OutlinedTextField(
                                value = selectedMoneyType?.nameMoneyType ?: "",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedMoney = !expandedMoney },
                                readOnly = true,
                                label = { Text("Тип оплаты", color = Color.White.copy(alpha = 0.7f)) },
                                trailingIcon = {
                                    IconButton(onClick = { expandedMoney = !expandedMoney }) {
                                        Icon(
                                            if (expandedMoney) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                colors = textFieldColors()
                            )

                            DropdownMenu(
                                expanded = expandedMoney,
                                onDismissRequest = { expandedMoney = false },
                                modifier = Modifier.background(Color(0xFF2D3243))
                            ) {
                                moneyTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.nameMoneyType ?: "", color = Color.White) },
                                        onClick = {
                                            selectedMoneyType = type
                                            expandedMoney = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Зарплата
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = zenStartVacancy,
                                onValueChange = { zenStartVacancy = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Зарплата от", color = Color.White.copy(alpha = 0.7f)) },
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = zenEndVacancy,
                                onValueChange = { zenEndVacancy = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("до", color = Color.White.copy(alpha = 0.7f)) },
                                colors = textFieldColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Валюта
                        Box {
                            OutlinedTextField(
                                value = selectedCurrency?.nameCurrency ?: "",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedCurrency = !expandedCurrency },
                                readOnly = true,
                                label = { Text("Валюта", color = Color.White.copy(alpha = 0.7f)) },
                                trailingIcon = {
                                    IconButton(onClick = { expandedCurrency = !expandedCurrency }) {
                                        Icon(
                                            if (expandedCurrency) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                colors = textFieldColors()
                            )

                            DropdownMenu(
                                expanded = expandedCurrency,
                                onDismissRequest = { expandedCurrency = false },
                                modifier = Modifier.background(Color(0xFF2D3243))
                            ) {
                                currencies.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(currency.nameCurrency ?: "", color = Color.White) },
                                        onClick = {
                                            selectedCurrency = currency
                                            expandedCurrency = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = hasMentor,
                                onCheckedChange = { hasMentor = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5399BC))
                            )
                            Text("Есть наставник", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Text("Адрес", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = cityAdress,
                            onValueChange = { cityAdress = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Город", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = streetAdress,
                            onValueChange = { streetAdress = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Улица", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = houseAdress,
                                onValueChange = { houseAdress = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Дом", color = Color.White.copy(alpha = 0.7f)) },
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = flatAdress,
                                onValueChange = { flatAdress = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Квартира", color = Color.White.copy(alpha = 0.7f)) },
                                colors = textFieldColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = indexAdress,
                            onValueChange = { indexAdress = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Индекс", color = Color.White.copy(alpha = 0.7f)) },
                            colors = textFieldColors()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (errorMessage != null) {
                            Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
                            onClick = { createVacancy() },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(28.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF4A90E2), Color(0xFF7B61FF))
                                        ),
                                        shape = RoundedCornerShape(28.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Создать вакансию",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    if (showTariffLimitDialog) {
        AlertDialog(
            onDismissRequest = { showTariffLimitDialog = false },
            title = {
                Text("Лимит вакансий исчерпан", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Вы достигли лимита вакансий в вашем тарифе.", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Перейдите на тариф Корпоративный для неограниченного количества вакансий!",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = {
                        showTariffLimitDialog = false
                        onNavigateToTariffs()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC))
                ) {
                    Text("Перейти к тарифам", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTariffLimitDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.6f))
                ) {
                    Text("Отмена", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}

fun getTariffName(tariffId: Int): String {
    return when (tariffId) {
        1 -> "Базовый"
        2 -> "Бизнес"
        3 -> "Корпоративный"
        else -> "Неизвестно"
    }
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFF5399BC),
    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
    focusedContainerColor = Color.White.copy(alpha = 0.1f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
    cursorColor = Color.White,
    focusedLabelColor = Color(0xFF5399BC),
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
)
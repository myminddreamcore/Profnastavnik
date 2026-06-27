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
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVacancyScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var moneyTypes by remember { mutableStateOf<List<MoneyType>>(emptyList()) }
    var currencies by remember { mutableStateOf<List<Currency>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }

    var nameVacancy by remember { mutableStateOf("") }
    var descriptionVacancy by remember { mutableStateOf("") }
    var timeVacancy by remember { mutableStateOf("") }
    var countUserVacancy by remember { mutableStateOf("") }
    var zenStartVacancy by remember { mutableStateOf("") }
    var zenEndVacancy by remember { mutableStateOf("") }
    var hasMentor by remember { mutableStateOf(false) }
    var selectedMoneyType by remember { mutableStateOf<MoneyType?>(null) }
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }

    var cityAdress by remember { mutableStateOf("") }
    var streetAdress by remember { mutableStateOf("") }
    var houseAdress by remember { mutableStateOf("") }
    var flatAdress by remember { mutableStateOf("") }
    var indexAdress by remember { mutableStateOf("") }

    var expandedMoney by remember { mutableStateOf(false) }
    var expandedCurrency by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            delay(1500)
            onSuccess()
        }
    }

    LaunchedEffect(Unit) {
        val money = api.getMoneyTypes()
        if (money != null) moneyTypes = money

        val currency = api.getCurrencies()
        if (currency != null) currencies = currency

        isLoadingData = false
    }

    fun createVacancy() {
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
        if (selectedCurrency == null) {
            errorMessage = "Выберите валюту"
            return
        }
        if (zenStartVacancy.isBlank() || zenEndVacancy.isBlank()) {
            errorMessage = "Введите диапазон зарплаты"
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

            val dto = CreateVacancyDTO(vacancy, adress)
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
                title = {
                    Text("Создать вакансию", color = Color.White)
                },
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
            if (isLoadingData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Описание", color = Color.White.copy(alpha = 0.7f)) },
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = timeVacancy,
                        onValueChange = { timeVacancy = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Длительность (например: 3 месяца)", color = Color.White.copy(alpha = 0.7f)) },
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
                                    text = {
                                        Text(
                                            type.nameMoneyType ?: "",
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedMoneyType = type
                                        expandedMoney = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                                    text = {
                                        Text(
                                            currency.nameCurrency ?: "",
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedCurrency = currency
                                        expandedCurrency = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                    Text(
                        "Адрес",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

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
                        Text(
                            errorMessage!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = { createVacancy() },
                        enabled = !isLoading,
                        modifier = Modifier
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
                                    Icon(
                                        Icons.Default.Add,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
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
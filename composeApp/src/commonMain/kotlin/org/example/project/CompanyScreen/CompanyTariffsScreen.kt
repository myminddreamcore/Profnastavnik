package org.example.project.CompanyScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CompanyPrice
import org.example.project.Models.CurrentUser
import org.example.project.Models.PricesForCompany
import org.example.project.UserScreen.textFieldColors

@Composable
fun CompanyTariffsScreen(
    api: ApiClient,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var prices by remember { mutableStateOf<List<PricesForCompany>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentCompanyPrice by remember { mutableStateOf<CompanyPrice?>(null) }
    var selectedPrice by remember { mutableStateOf<PricesForCompany?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var isPaymentSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val companyId = CurrentUser.id ?: 0
        val pricesResult = api.getAllCompanyTariffs()
        if (pricesResult != null) {
            prices = pricesResult
        }
        val companyPriceResult = api.getCompanyTariff(companyId)
        if (companyPriceResult != null) {
            currentCompanyPrice = companyPriceResult
        }
        isLoading = false
    }

    fun validateCard(): Boolean {
        val cleanCardNumber = cardNumber.replace(" ", "")
        if (cleanCardNumber.length != 16 || !cleanCardNumber.all { it.isDigit() }) {
            paymentError = "Введите корректный номер карты (16 цифр)"
            return false
        }

        val expiryParts = expiryDate.split("/")
        if (expiryParts.size != 2) {
            paymentError = "Введите дату в формате ММ/ГГ"
            return false
        }
        val month = expiryParts[0].toIntOrNull()
        val year = expiryParts[1].toIntOrNull()
        if (month == null || year == null || month !in 1..12 || year < 25 || year > 35) {
            paymentError = "Введите корректную дату (ММ/ГГ)"
            return false
        }

        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            paymentError = "Введите корректный CVV код (3 цифры)"
            return false
        }

        if (cardholderName.isBlank() || cardholderName.length < 3) {
            paymentError = "Введите имя владельца карты"
            return false
        }

        paymentError = null
        return true
    }

    fun formatCardNumber(input: String): String {
        val clean = input.replace(" ", "")
        return clean.chunked(4).joinToString(" ").take(19)
    }

    fun formatExpiryDate(input: String): String {
        val clean = input.replace("/", "")
        return when {
            clean.length >= 2 -> "${clean.take(2)}/${clean.drop(2).take(2)}"
            else -> clean
        }.take(5)
    }

    fun buyTariff() {
        if (!validateCard()) return

        scope.launch {
            isProcessing = true
            delay(2000)

            val companyId = CurrentUser.id ?: 0
            val newCompanyPrice = CompanyPrice(
                id = 0,
                idCompany = companyId,
                idPrice = selectedPrice?.id
            )
            val result = api.addNewCompanyPrice(newCompanyPrice)
            isProcessing = false

            if (result != null) {
                isPaymentSuccess = true
                delay(1500)
                showPaymentDialog = false
                onSuccess()
            } else {
                paymentError = "Ошибка при оформлении. Попробуйте позже."
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
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
                    "Тарифы для компании",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                if (currentCompanyPrice != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF5399BC).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Ваш текущий тариф",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    prices.find { it.id == currentCompanyPrice?.idPrice }?.namePrice ?: "Базовый",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(prices) { price ->
                        CompanyTariffCard(
                            price = price,
                            isCurrent = currentCompanyPrice?.idPrice == price.id,
                            onSelect = {
                                if (currentCompanyPrice?.idPrice != price.id) {
                                    selectedPrice = price
                                    showPaymentDialog = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showPaymentDialog && selectedPrice != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isProcessing) {
                    showPaymentDialog = false
                    paymentError = null
                    cardNumber = ""
                    expiryDate = ""
                    cvv = ""
                    cardholderName = ""
                }
            },
            title = {
                Column {
                    Text(
                        "Оплата тарифа",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        selectedPrice?.namePrice ?: "",
                        color = Color(0xFF5399BC),
                        fontSize = 14.sp
                    )
                }
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("К оплате:", color = Color.White, fontSize = 16.sp)
                            Text(
                                "${selectedPrice?.cost} ₽",
                                color = Color(0xFFFFB74D),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = {
                            cardNumber = formatCardNumber(it)
                            paymentError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Номер карты", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("1234 5678 9012 3456", color = Color.White.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        leadingIcon = {
                            Icon(Icons.Default.CreditCard, null, tint = Color(0xFF5399BC))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = {
                                expiryDate = formatExpiryDate(it)
                                paymentError = null
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("ММ/ГГ", color = Color.White.copy(alpha = 0.7f)) },
                            placeholder = { Text("12/25", color = Color.White.copy(alpha = 0.5f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors()
                        )

                        OutlinedTextField(
                            value = cvv,
                            onValueChange = {
                                cvv = it.take(3)
                                paymentError = null
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("CVV", color = Color.White.copy(alpha = 0.7f)) },
                            placeholder = { Text("123", color = Color.White.copy(alpha = 0.5f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cardholderName,
                        onValueChange = {
                            cardholderName = it.uppercase()
                            paymentError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Имя владельца", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("IVAN IVANOV", color = Color.White.copy(alpha = 0.5f)) },
                        colors = textFieldColors()
                    )

                    if (paymentError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            paymentError!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Данные защищены SSL шифрованием",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { buyTariff() },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isProcessing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Обработка платежа...", color = Color.White)
                        }
                    } else {
                        Text("Оплатить ${selectedPrice?.cost} ₽", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        if (!isProcessing) {
                            showPaymentDialog = false
                            paymentError = null
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Отмена", color = Color.White)
                }
            }
        )
    }

    if (isPaymentSuccess) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Оплата прошла успешно!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "Ваш тариф был обновлен. Теперь вам доступны новые возможности!",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = { isPaymentSuccess = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC))
                ) {
                    Text("Отлично!", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun CompanyTariffCard(
    price: PricesForCompany,
    isCurrent: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isCurrent) { onSelect() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                Color(0xFF5399BC).copy(alpha = 0.3f)
            else
                Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    price.namePrice ?: "Тариф",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isCurrent) {
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Текущий",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Цена
            if (price.cost == 0) {
                Text(
                    "Бесплатно",
                    color = Color(0xFF4CAF50),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    "${price.cost} ₽",
                    color = Color(0xFFFFB74D),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Количество вакансий
            if (price.countVacancy == null) {
                Text(
                    "Неограниченное количество вакансий",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            } else {
                Text(
                    "${price.countVacancy} вакансий",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            // Количество откликов
            if (price.countResponce == null) {
                Text(
                    "Неограниченное количество откликов",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            } else {
                Text(
                    "До ${price.countResponce} откликов в месяц",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }

            // Описание
            if (!price.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                val lines = price.description.split("\\n")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    lines.forEach { line ->
                        if (line.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "•",
                                    color = Color(0xFF5399BC),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    line.trim(),
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isCurrent) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Выбрать", color = Color.White)
                }
            } else {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false
                ) {
                    Text("Активен", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}
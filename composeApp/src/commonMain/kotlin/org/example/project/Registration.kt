package org.example.project

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.Director
import org.example.project.Models.Student
import org.example.project.Models.User

val InputFieldColor = Color(0xFF5399BC)
val ButtonColor = Color(0xFF2E79A3)

@Composable
fun RegistrationScreen(
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val apiClient = remember { ApiClient() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Стажер") }

    var studentName by remember { mutableStateOf("") }
    var studentSurname by remember { mutableStateOf("") }
    var studentPatronymic by remember { mutableStateOf("") }

    var companyName by remember { mutableStateOf("") }
    var companyPhone by remember { mutableStateOf("") }
    var companyCity by remember { mutableStateOf("") }

    var showVerificationCode by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }
    var isSendingCode by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

    fun sendVerificationCode() {
        scope.launch {
            if (!isValidEmail(email)) {
                emailError = "Введите корректный email"
                return@launch
            }

            if (password.length < 6) {
                passwordError = "Пароль должен содержать минимум 6 символов"
                return@launch
            }

            if (password != confirmPassword) {
                passwordError = "Пароли не совпадают"
                return@launch
            }

            if (selectedRole == "Стажер") {
                if (studentSurname.isBlank() || studentName.isBlank()) {
                    nameError = "Заполните имя и фамилию"
                    return@launch
                }
            } else if (selectedRole == "Работодатель") {
                if (companyName.isBlank()) {
                    nameError = "Введите название компании"
                    return@launch
                }
            }

            emailError = null
            passwordError = null
            nameError = null

            isSendingCode = true

            val success = apiClient.sendVerificationCode(email)
            isSendingCode = false

            if (success) {
                isCodeSent = true
                showVerificationCode = true
                countdown = 60
                snackbarHostState.showSnackbar("Код подтверждения отправлен на $email")

                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
            } else {
                snackbarHostState.showSnackbar("Ошибка отправки кода. Попробуйте позже.")
            }
        }
    }

    fun verifyAndRegister() {
        scope.launch {
            if (verificationCode.length != 4) {
                snackbarHostState.showSnackbar("Введите 4-значный код")
                return@launch
            }

            isVerifying = true

            val isCodeValid = apiClient.verifyCode(email, verificationCode)

            if (!isCodeValid) {
                snackbarHostState.showSnackbar("Неверный код подтверждения")
                isVerifying = false
                return@launch
            }

            val newUser = User(
                emailUser = email,
                passwordUser = password,
                roleUser = selectedRole
            )

            val registeredUser = apiClient.register(newUser)

            if (registeredUser == null) {
                snackbarHostState.showSnackbar("Ошибка регистрации. Попробуйте позже.")
                isVerifying = false
                return@launch
            }

            if (selectedRole == "Стажер") {
                val student = Student(
                    idStudent = 0,
                    emailStudent = email,
                    nameStudent = studentName,
                    surnameStudent = studentSurname,
                    patronymicStudent = studentPatronymic,
                    statusStudent = "Активен"
                )
                apiClient.createStudent(student)
            } else if (selectedRole == "Работодатель") {
                val director = Director(
                    idDirector = 0,
                    emailDirector = email,
                    nameCompanyDirector = companyName,
                    nameDirector = "",
                    surnameDirector = "",
                    patronymicDirector = "",
                    phoneDirector = companyPhone,
                    cityDirector = companyCity,
                    statusDirector = "Активен"
                )
                apiClient.createDirector(director)
            }

            isVerifying = false
            snackbarHostState.showSnackbar("Регистрация успешно завершена!")
            delay(1000)
            onSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BgGradientStart, BgGradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Верхняя панель со стрелкой назад
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "ПРОФНаставник",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Регистрация",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 24.dp)
                        )

                        Text(
                            text = "Выберите роль:",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RoleOption(
                                "Работодатель",
                                Icons.Default.Business,
                                selectedRole
                            ) { selectedRole = it }
                            Spacer(modifier = Modifier.width(12.dp))
                            RoleOption(
                                "Стажер",
                                Icons.Default.Person,
                                selectedRole
                            ) { selectedRole = it }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedRole == "Стажер") {
                            RegistrationInputField("Имя", studentName) { studentName = it }
                            RegistrationInputField("Фамилия", studentSurname) { studentSurname = it }
                            RegistrationInputField("Отчество", studentPatronymic) { studentPatronymic = it }
                        }

                        if (selectedRole == "Работодатель") {
                            RegistrationInputField("Название компании", companyName) { companyName = it }
                            RegistrationInputField("Телефон", companyPhone) { companyPhone = it }
                            RegistrationInputField("Город", companyCity) { companyCity = it }
                        }

                        RegistrationInputField("Email", email) { email = it }
                        if (emailError != null) {
                            Text(
                                emailError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        RegistrationInputField(
                            "Пароль",
                            password,
                            isPassword = true
                        ) { password = it }

                        RegistrationInputField(
                            "Подтвердите пароль",
                            confirmPassword,
                            isPassword = true
                        ) { confirmPassword = it }

                        if (passwordError != null) {
                            Text(
                                passwordError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        if (nameError != null) {
                            Text(
                                nameError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (showVerificationCode) {
                            Column {
                                Text(
                                    "Введите код из письма:",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                RegistrationInputField(
                                    "Код подтверждения",
                                    verificationCode
                                ) { verificationCode = it }

                                if (countdown > 0) {
                                    Text(
                                        "Отправить повторно через ${countdown}с",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                } else {
                                    TextButton(
                                        onClick = {
                                            if (!isSendingCode) {
                                                sendVerificationCode()
                                            }
                                        },
                                        enabled = !isSendingCode
                                    ) {
                                        Text(
                                            if (isSendingCode) "Отправка..." else "Отправить код повторно",
                                            color = Color(0xFF5399BC)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (!showVerificationCode) {
                            Button(
                                onClick = { sendVerificationCode() },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(54.dp)
                                    .width(220.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                enabled = !isSendingCode
                            ) {
                                if (isSendingCode) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        text = "Отправить код",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { verifyAndRegister() },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(54.dp)
                                    .width(220.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                enabled = !isVerifying
                            ) {
                                if (isVerifying) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        text = "Подтвердить и зарегистрироваться",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Текст "Уже есть аккаунт? Войти"
                        Text(
                            text = "Уже есть аккаунт? Войти",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 16.dp)
                                .clickable { onNavigateBack() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun RoleOption(
    role: String,
    icon: ImageVector,
    selectedRole: String,
    onSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = (role == selectedRole),
            onClick = { onSelect(role) },
            colors = RadioButtonDefaults.colors(
                unselectedColor = Color.White,
                selectedColor = Color.White
            )
        )
        Text(
            text = role,
            color = Color.White,
            fontSize = 14.sp
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
                .padding(start = 4.dp)
        )
    }
}

@Composable
fun RegistrationInputField(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputFieldColor,
                unfocusedContainerColor = InputFieldColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )
    }
}
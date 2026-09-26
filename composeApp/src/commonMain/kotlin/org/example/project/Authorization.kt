package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.CurrentUser
import org.example.project.Models.User

val BgGradientStart = Color(0xFF388EAB)
val BgGradientEnd = Color(0xFFD1E4E9)
val CardBg = Color(0xFFFFFFFF).copy(alpha = 0.2f)

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onCompanySuccess: () -> Unit,
    onAdminSuccess: () -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotPassword by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var isSendingCode by remember { mutableStateOf(false) }
    var isResettingPassword by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val api = remember { ApiClient() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Верхняя панель с кнопкой назад
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (showForgotPassword) {
                                showForgotPassword = false
                                isCodeSent = false
                            } else {
                                onNavigateToRegistration()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (showForgotPassword) "Восстановление пароля" else "ПРОФНаставник",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (showForgotPassword) {
                    // ЭКРАН ВОССТАНОВЛЕНИЯ ПАРОЛЯ
                    ForgotPasswordContent(
                        email = email,
                        onEmailChange = { email = it },
                        verificationCode = verificationCode,
                        onVerificationCodeChange = { verificationCode = it },
                        newPassword = newPassword,
                        onNewPasswordChange = { newPassword = it },
                        confirmNewPassword = confirmNewPassword,
                        onConfirmNewPasswordChange = { confirmNewPassword = it },
                        isCodeSent = isCodeSent,
                        isSendingCode = isSendingCode,
                        isResettingPassword = isResettingPassword,
                        countdown = countdown,
                        onSendCode = {
                            scope.launch {
                                if (email.isEmpty()) {
                                    snackbarHostState.showSnackbar("Введите email")
                                    return@launch
                                }
                                isSendingCode = true
                                val success = api.sendVerificationCode(email)
                                isSendingCode = false
                                if (success) {
                                    isCodeSent = true
                                    countdown = 60
                                    snackbarHostState.showSnackbar("Код отправлен на $email")
                                    while (countdown > 0) {
                                        delay(1000)
                                        countdown--
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("Ошибка отправки кода")
                                }
                            }
                        },
                        onResetPassword = {
                            scope.launch {
                                if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                                    snackbarHostState.showSnackbar("Заполните все поля")
                                    return@launch
                                }
                                if (newPassword.length < 6) {
                                    snackbarHostState.showSnackbar("Пароль должен быть минимум 6 символов")
                                    return@launch
                                }
                                if (newPassword != confirmNewPassword) {
                                    snackbarHostState.showSnackbar("Пароли не совпадают")
                                    return@launch
                                }
                                if (verificationCode.length != 4) {
                                    snackbarHostState.showSnackbar("Введите 4-значный код")
                                    return@launch
                                }

                                isResettingPassword = true
                                // Проверяем код
                                val isCodeValid = api.verifyCode(email, verificationCode)
                                if (!isCodeValid) {
                                    snackbarHostState.showSnackbar("Неверный код подтверждения")
                                    isResettingPassword = false
                                    return@launch
                                }

                                // Обновляем пароль
                                val success = api.resetPassword(email, newPassword)
                                isResettingPassword = false
                                if (success) {
                                    snackbarHostState.showSnackbar("Пароль успешно изменен!")
                                    showForgotPassword = false
                                    isCodeSent = false
                                    password = newPassword
                                } else {
                                    snackbarHostState.showSnackbar("Ошибка смены пароля")
                                }
                            }
                        },
                        onBack = {
                            showForgotPassword = false
                            isCodeSent = false
                        }
                    )
                } else {
                    // ЭКРАН ВХОДА
                    LoginContent(
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        onLogin = {
                            if (email.isEmpty() || password.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Заполните поля логин и пароль!")
                                }
                            } else {
                                scope.launch {
                                    try {
                                        val userRequest = User(emailUser = email, passwordUser = password)
                                        val result = api.login(userRequest)

                                        if (result != null) {
                                            snackbarHostState.showSnackbar("Успешный вход!")
                                            if (result.roleUser == "Стажер") {
                                                val userRequest2 = User(emailUser = email)
                                                val result2 = api.getUser(userRequest2)
                                                CurrentUser.id = result2?.idStudent
                                                CurrentUser.email = result2?.emailStudent
                                                CurrentUser.role = "student"
                                                CurrentUser.isAuthorized = true
                                                onSuccess()
                                            } else if (result.roleUser == "Работодатель") {
                                                val userRequest2 = User(emailUser = email)
                                                val result2 = api.getCompany(userRequest2)
                                                CurrentUser.id = result2?.idDirector
                                                CurrentUser.email = result2?.emailDirector
                                                CurrentUser.role = "company"
                                                CurrentUser.isAuthorized = true
                                                onCompanySuccess()
                                            } else if (result.roleUser == "Администратор") {CurrentUser.role = "admin"
                                                CurrentUser.email = result?.emailUser
                                                onAdminSuccess()
                                            }
                                        } else {
                                            snackbarHostState.showSnackbar("Неверный логин или пароль!")
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Проблема с сетью!")
                                    }
                                }
                            }
                        },
                        onForgotPassword = {
                            showForgotPassword = true
                            isCodeSent = false
                        },
                        onNavigateToRegistration = onNavigateToRegistration
                    )
                }
            }
        }
    }
}

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Войти",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 32.dp)
            )

            LoginInputField("Введите ваш email:", email) { onEmailChange(it) }
            LoginInputField(
                "Введите ваш пароль:",
                password,
                isPassword = true
            ) { onPasswordChange(it) }

            // Ссылка "Забыли пароль?"
            Text(
                text = "Забыли пароль?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPassword() }
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(54.dp)
                    .width(180.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E79A3)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Войти",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет аккаунта? Зарегистрироваться",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateToRegistration() }
            )
        }
    }
}

@Composable
fun ForgotPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmNewPassword: String,
    onConfirmNewPasswordChange: (String) -> Unit,
    isCodeSent: Boolean,
    isSendingCode: Boolean,
    isResettingPassword: Boolean,
    countdown: Int,
    onSendCode: () -> Unit,
    onResetPassword: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Восстановление пароля",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Введите email, на который будет отправлен код",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LoginInputField("Email:", email) { onEmailChange(it) }

            if (isCodeSent) {
                LoginInputField("Код из письма:", verificationCode) { onVerificationCodeChange(it) }
                LoginInputField("Новый пароль:", newPassword, isPassword = true) { onNewPasswordChange(it) }
                LoginInputField("Подтвердите пароль:", confirmNewPassword, isPassword = true) { onConfirmNewPasswordChange(it) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isCodeSent) {
                Button(
                    onClick = onSendCode,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(54.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E79A3)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isSendingCode
                ) {
                    if (isSendingCode) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            text = "Отправить код",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Button(
                    onClick = onResetPassword,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(54.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E79A3)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isResettingPassword
                ) {
                    if (isResettingPassword) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            text = "Сменить пароль",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (countdown > 0) {
                Text(
                    text = "Отправить повторно через ${countdown}с",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (isCodeSent) {
                TextButton(
                    onClick = onSendCode,
                    enabled = !isSendingCode
                ) {
                    Text(
                        text = if (isSendingCode) "Отправка..." else "Отправить код повторно",
                        color = Color(0xFF5399BC)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginInputField(
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
                focusedContainerColor = Color(0xFF5399BC),
                unfocusedContainerColor = Color(0xFF5399BC),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )
    }
}
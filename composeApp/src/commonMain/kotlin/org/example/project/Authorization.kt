package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.CurrentUser
import org.example.project.Models.User

val BgGradientStart = Color(0xFF388EAB)
val BgGradientEnd = Color(0xFFD1E4E9)
val CardBg = Color(0xFFFFFFFF).copy(alpha = 0.2f)
@Composable
fun LoginScreen(onSuccess: () -> Unit, onCompanySuccess : () ->Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "ПРОФНаставник",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
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

                        LoginInputField("Введите ваш email:", email) { email = it }
                        LoginInputField(
                            "Введите ваш пароль:",
                            password,
                            isPassword = true
                        ) { password = it }

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = {
                                if (email == "" || password == "") {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Заполните поля логин и пароль!")
                                    }
                                } else {
                                    scope.launch {
                                        try {
                                            val userRequest =
                                                User(emailUser = email, passwordUser = password)
                                            val result = api.login(userRequest)

                                            if (result != null) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Успешный вход!")
                                                }
                                                if (result.roleUser == "Стажер")
                                                {
                                                    val userRequest2 =
                                                        User(emailUser = email)
                                                    val result = api.getUser(userRequest2)
                                                    CurrentUser.id = result?.idStudent
                                                    CurrentUser.email = result?.emailStudent
                                                    CurrentUser.role = "Стажер"
                                                    CurrentUser.isAuthorized = true
                                                    onSuccess()


                                                }
                                                else if (result.roleUser == "Работодатель")
                                                {
                                                    val userRequest2 =
                                                        User(emailUser = email)
                                                    val result = api.getCompany(userRequest2)
                                                    CurrentUser.id = result?.idDirector
                                                    CurrentUser.email = result?.emailDirector
                                                    CurrentUser.role = "Работодатель"
                                                    CurrentUser.isAuthorized = true
                                                    onCompanySuccess()
                                                }
                                                else if (result.roleUser == "Администратор")
                                                {

                                                }
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Неверный логин или пароль!")
                                                }
                                            }
                                        } catch (e: Exception) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Проблема с сетью!")
                                            }
                                        }
                                    }
                                }
                            },
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
                    }
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

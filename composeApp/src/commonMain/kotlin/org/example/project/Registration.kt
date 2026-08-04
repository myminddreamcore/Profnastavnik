import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.User

val InputFieldColor = Color(0xFF5399BC)
val ButtonColor = Color(0xFF2E79A3)

@Composable
fun RegistrationScreen(onSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    val apiClient = remember { ApiClient() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Работодатель") }
    val snackbarHostState = remember { SnackbarHostState() }
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
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "ПРОФНаставник",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

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
                                Icons.Default.Person,
                                selectedRole
                            ) { selectedRole = it }
                            Spacer(modifier = Modifier.width(12.dp))
                            RoleOption(
                                "Стажер",
                                Icons.Default.Search,
                                selectedRole
                            ) { selectedRole = it }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        RegistrationInputField("Введите ваш email:", email) { email = it }
                        RegistrationInputField(
                            "Введите ваш пароль:",
                            password,
                            isPassword = true
                        ) { password = it }
                        RegistrationInputField(
                            "Подтвердите ваш пароль:",
                            confirmPassword,
                            isPassword = true
                        ) { confirmPassword = it }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (email.isEmpty() || password.isEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Заполните поля логин и пароль!")
                                    }
                                }
                                if (password != confirmPassword) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Пароли не совпадают!")
                                    }
                                }

                                scope.launch {
                                    val newUser = User(
                                        emailUser = email,
                                        passwordUser = password,
                                        roleUser = selectedRole
                                    )

                                    val success = apiClient.register(newUser)

                                    if (success == null) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Успешная регистрация!")
                                            onSuccess()
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(success)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .height(54.dp)
                                .width(220.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Зарегистрироваться",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
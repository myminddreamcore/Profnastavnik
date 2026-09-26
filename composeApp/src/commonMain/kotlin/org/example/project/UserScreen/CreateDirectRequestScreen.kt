package org.example.project.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.CreateRequestCompany
import org.example.project.Models.CreateRequestUser
import org.example.project.Models.CurrentUser

@Composable
fun CreateDirectRequestScreen(
    api: ApiClient,
    targetType: String, // "user" или "company"
    targetId: Int,
    targetName: String?,
    userRole: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var description by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun sendRequest() {
        if (description.isBlank()) {
            error = "Опишите причину жалобы"
            return
        }

        scope.launch {
            isSending = true
            error = null

            val success = if (userRole == "student") {
                val request = CreateRequestUser(
                    idStudent = CurrentUser.id ?: 0,
                    idFeedback = 0,  // <-- БЕЗ ОТЗЫВА
                    descriptionRequest = description
                )
                api.createUserRequest(request)
            } else {
                val request = CreateRequestCompany(
                    idCompany = CurrentUser.id ?: 0,
                    idFeedback = 0,  // <-- БЕЗ ОТЗЫВА
                    descriptionRequset = description
                )
                api.createCompanyRequest(request)
            }

            isSending = false

            if (success) {
                snackbarHostState.showSnackbar("Жалоба отправлена")
                onSuccess()
            } else {
                error = "Ошибка отправки"
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Text("Подать жалобу", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        Text("Жалоба на пользователя, на работу приложения и прочее", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Причина жалобы(если жалоба на компанию или сотрудника просьба указать его почту или ФИО(название):", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it; error = null },
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            placeholder = { Text("Опишите причину...", color = Color.White.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF5399BC),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                            )
                        )

                        if (error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(error!!, color = Color.Red, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { sendRequest() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isSending
                        ) {
                            if (isSending) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.Report, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Отправить жалобу", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
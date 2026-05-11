package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.example.project.Models.Chat
import org.example.project.Models.ChatDTO
import org.example.project.Models.CurrentUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    api: ApiClient,
    chatDTO: ChatDTO,
    onBack: () -> Unit,
    onNavigateToVacancy: (Int) -> Unit,
    onNavigateToCompany: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf<List<ChatDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val chat = chatDTO.chat
    val name = chatDTO.nameCompany ?: chatDTO.nameVacancy ?: "Чат"
    val isCompanyChat = chat.idDirector != null
    val recipientId = if (isCompanyChat) chat.idDirector else null
    val recipientEmail = if (!isCompanyChat) chat.emailAdmin else null

    fun loadMessages() {
        scope.launch {
            isLoading = true
            val userId = CurrentUser.id ?: 0
            val vacancyId = chat.idVacancy ?: 0

            val result = if (isCompanyChat && recipientId != null) {
                api.getChatMessagesCompany(userId, recipientId, vacancyId)
            } else if (!isCompanyChat && recipientEmail != null) {
                api.getChatMessagesAdmin(userId, recipientEmail)
            } else {
                null
            }

            if (result != null) {
                messages = result
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadMessages()
    }

    fun sendMessage() {
        if (inputText.isBlank() || isSending) return

        scope.launch {
            isSending = true
            val userId = CurrentUser.id ?: 0

            val newMessage = Chat(
                idChat = 0,
                idUser = userId,
                textChat = inputText,
                statusChat = "Отправлено",
                sendAtChat = null,
                idVacancy = chat.idVacancy,
                idDirector = chat.idDirector,
                emailAdmin = chat.emailAdmin,
                senderChat = "Стажер"
            )

            val success = api.sendMessage(newMessage)

            if (success) {
                inputText = ""
                delay(500)
                loadMessages()
            }
            isSending = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable {
                            if (isCompanyChat && recipientId != null) {
                                onNavigateToCompany(recipientId)
                            }
                        }
                    ) {
                        Text(
                            name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            chatDTO.nameVacancy ?: "",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                chat.idVacancy?.let { onNavigateToVacancy(it) }
                            }
                        )
                    }
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages.reversed()) { msg ->
                            MessageBubble(
                                message = msg.chat,
                                isMe = msg.chat.senderChat == "Стажер"
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Сообщение...", color = Color.White.copy(alpha = 0.5f)) },
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { sendMessage() },
                                enabled = !isSending && inputText.isNotBlank(),
                                modifier = Modifier
                                    .background(Color(0xFF5399BC), CircleShape)
                                    .size(48.dp)
                            ) {
                                Icon(
                                    if (isSending) Icons.Default.HourglassEmpty else Icons.Default.Send,
                                    null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MessageBubble(
    message: Chat,
    isMe: Boolean
) {
    val isRead = message.statusChat == "Прочитано"
    val isSent = message.statusChat == "Отправлено"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                color = if (isMe) Color(0xFF5399BC) else Color.White.copy(alpha = 0.15f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        message.textChat ?: "",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.sendAtChat != null) {
                            Text(
                                formatTime(message.sendAtChat),
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        if (isMe) {
                            when {
                                isRead -> {
                                    Icon(
                                        Icons.Default.DoneAll,
                                        null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                isSent -> {
                                    Icon(
                                        Icons.Default.Done,
                                        null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
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

fun formatTime(dateString: String?): String {
    if (dateString.isNullOrBlank()) return ""
    return try {
        val parts = dateString.split("T")
        val timeParts = parts.getOrElse(1) { "00:00:00" }.split(":")
        "${timeParts[0]}:${timeParts[1].padStart(2, '0')}"
    } catch (e: Exception) {
        ""
    }
}
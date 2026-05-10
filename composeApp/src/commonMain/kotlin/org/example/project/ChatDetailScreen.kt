package org.example.project

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.ChatDTO
//import org.example.project.Models.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    api: ApiClient,
    chat: ChatDTO?,
    onBack: () -> Unit
) {
//    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(chat) {
        if (chat != null) {
            // Загрузи сообщения чата
            // val result = api.getChatMessages(chat.chat.idChat)
            // messages = result ?: emptyList()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            chat?.nameCompany ?: chat?.nameVacancy ?: "Чат",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Онлайн",
                            color = Color.Green,
                            fontSize = 12.sp
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
                    // Список сообщений
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        reverseLayout = true
                    ) {
//                        items(messages.reversed()) { message ->
//                            MessageBubble(message = message)
//                        }
                    }

                    // Поле ввода
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Сообщение...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    scope.launch {
                                        // Отправить сообщение
                                        // api.sendMessage(chat?.chat?.idChat ?: 0, inputText)
                                        inputText = ""
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(Color(0xFF5399BC), CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(Icons.Default.Send, null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
//
//@Composable
//fun MessageBubble(message: Message) {
//    val isMe = message.senderId == CurrentUser.id
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
//    ) {
//        Surface(
//            shape = RoundedCornerShape(
//                topStart = 16.dp,
//                topEnd = 16.dp,
//                bottomStart = if (isMe) 16.dp else 4.dp,
//                bottomEnd = if (isMe) 4.dp else 16.dp
//            ),
//            color = if (isMe) Color(0xFF5399BC) else Color.White.copy(alpha = 0.15f)
//        ) {
//            Column(modifier = Modifier.padding(12.dp)) {
//                Text(
//                    message.text,
//                    color = Color.White,
//                    fontSize = 14.sp
//                )
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    Text(
//                        message.time,
//                        color = Color.White.copy(alpha = 0.5f),
//                        fontSize = 10.sp
//                    )
//                    if (isMe) {
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Icon(
//                            if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
//                            null,
//                            tint = if (message.isRead) Color(0xFF5399BC) else Color.White.copy(alpha = 0.5f),
//                            modifier = Modifier.size(14.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
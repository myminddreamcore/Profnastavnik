package org.example.project.UserScreen

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
import org.example.project.Models.Chat
import org.example.project.Models.ChatDTO
import org.example.project.Models.CurrentUser
import org.example.project.Models.Intership

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    api: ApiClient,
    chatDTO: ChatDTO,
    onBack: () -> Unit,
    onNavigateToVacancy: (Int) -> Unit,
    onNavigateToCompany: (Int) -> Unit,
    onNavigateToUser: (Int) -> Unit,
    isCompany: Boolean = false
) {
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf<List<ChatDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var isFirstLoad by remember { mutableStateOf(true) }

    var internship by remember { mutableStateOf<Intership?>(null) }
    var isLoadingInternship by remember { mutableStateOf(false) }
    var isStartingInternship by remember { mutableStateOf(false) }
    var isEndingInternship by remember { mutableStateOf(false) }
    var showStartDialog by remember { mutableStateOf(false) }
    var internshipDays by remember { mutableStateOf(0) }

    val chat = chatDTO.chat
    val userRole = CurrentUser.role ?: "student"

    val isAdminChat = chat.emailAdmin != null
    val isCompanyChat = chat.idDirector != null

    // ========== ОПРЕДЕЛЯЕМ ОТОБРАЖАЕМОЕ ИМЯ ==========
    val displayName = when (userRole) {
        "admin" -> {
            if (isCompanyChat) {
                chatDTO.nameCompany ?: chatDTO.nameVacancy ?: "Компания"
            } else {
                chatDTO.fioUser ?: chatDTO.nameVacancy ?: "Пользователь"
            }
        }
        "company" -> {
            if (isAdminChat) {
                chatDTO.chat.emailAdmin ?: "Администратор"
            } else {
                chatDTO.fioUser ?: chatDTO.nameVacancy ?: "Студент"
            }
        }
        else -> { // student
            if (isCompanyChat) {
                chatDTO.nameCompany ?: chatDTO.nameVacancy ?: "Компания"
            } else {
                chatDTO.chat.emailAdmin ?: "Администратор"
            }
        }
    }

    // ========== ЗАГРУЗКА СТАЖИРОВКИ ==========
    fun loadInternship() {
        if (userRole != "company") return

        scope.launch {
            isLoadingInternship = true
            val userId = chat.idUser ?: 0
            val companyId = CurrentUser.id ?: 0
            val vacancyId = chat.idVacancy ?: 0

            if (userId > 0 && vacancyId > 0) {
                val result = api.getUserIntership(userId, companyId, vacancyId)
                internship = result
                if (result?.statusIntership == "Идет") {
                    val days = api.getInternshipDays(result.idIntership)
                    if (days != null) {
                        internshipDays = days
                    }
                }
            }
            isLoadingInternship = false
        }
    }

    fun startInternship() {
        scope.launch {
            isStartingInternship = true
            val userId = chat.idUser ?: 0
            val companyId = CurrentUser.id ?: 0
            val vacancyId = chat.idVacancy ?: 0

            val newInternship = Intership(
                idIntership = 0,
                idUser = userId,
                idCompany = companyId,
                dateStartIntership = null,
                dateEndIntership = null,
                statusIntership = null,
                responseId = null,
                idVacancy = vacancyId
            )

            val result = api.startInternship(newInternship)
            if (result != null) {
                internship = result
                showStartDialog = false
                val days = api.getInternshipDays(result.idIntership)
                if (days != null) {
                    internshipDays = days
                }
            }
            isStartingInternship = false
        }
    }

    fun endInternship() {
        scope.launch {
            isEndingInternship = true
            internship?.idIntership?.let { id ->
                val result = api.endInternship(id)
                if (result != null) {
                    internship = result
                    internshipDays = 0
                }
            }
            isEndingInternship = false
        }
    }

    // ========== ЗАГРУЗКА СООБЩЕНИЙ ==========
    fun loadMessages() {
        scope.launch {
            isLoading = true

            val userId = if (CurrentUser.role == "student") {
                CurrentUser.id ?: 0
            } else {
                chat.idUser ?: 0
            }
            val companyId = if (CurrentUser.role == "company") {
                CurrentUser.id ?: 0
            } else {
                chat.idDirector ?: 0
            }
            val vacancyId = chat.idVacancy ?: 0

            val result = when (userRole) {
                "admin" -> {
                    if (isAdminChat && chat.emailAdmin != null) {
                        if (isCompanyChat) {
                            api.getChatMessagesCompanyAdmin(companyId, chat.emailAdmin!!)
                        } else {
                            api.getChatMessagesAdmin(userId, chat.emailAdmin!!)
                        }
                    } else {
                        null
                    }
                }
                "company" -> {
                    if (isAdminChat && chat.emailAdmin != null) {
                        api.getChatMessagesCompanyAdmin(companyId, chat.emailAdmin!!)
                    } else if (isCompanyChat) {
                        api.getChatMessagesCompany(userId, companyId, vacancyId)
                    } else {
                        null
                    }
                }
                else -> { // student
                    if (isAdminChat && chat.emailAdmin != null) {
                        api.getChatMessagesAdmin(userId, chat.emailAdmin!!)
                    } else if (isCompanyChat) {
                        val companyId = chat.idDirector ?: 0
                        api.getChatMessagesCompany(userId, companyId, vacancyId)
                    } else {
                        null
                    }
                }
            }

            if (result != null) {
                val unreadMessages = result.filter { msg ->
                    val isFromOther = when (userRole) {
                        "company" -> msg.chat.senderChat == "Стажер" || msg.chat.senderChat == "Админ"
                        "admin" -> msg.chat.senderChat == "Стажер" || msg.chat.senderChat == "Работодатель"
                        else -> msg.chat.senderChat == "Работодатель" || msg.chat.senderChat == "Админ"
                    }
                    isFromOther && msg.chat.statusChat != "Прочитано"
                }

                unreadMessages.forEach { msg ->
                    msg.chat.idChat?.let { chatId ->
                        api.markMessageAsRead(chatId)
                    }
                }

                messages = result

                if (isFirstLoad) {
                    isFirstLoad = false
                    loadInternship()
                }
            }
            isLoading = false
        }
    }

    fun startPolling() {
        scope.launch {
            while (true) {
                delay(5000)
                if (!isLoading) {
                    loadMessages()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadMessages()
        startPolling()
    }

    // ========== ОТПРАВКА СООБЩЕНИЯ ==========
    fun sendMessage() {
        if (inputText.isBlank() || isSending) return

        scope.launch {
            isSending = true

            val userId = if (CurrentUser.role == "student") {
                CurrentUser.id ?: null
            } else {
                chat.idUser ?: null
            }
            val companyId = if (CurrentUser.role == "company") {
                CurrentUser.id ?: null
            } else {
                chat.idDirector ?: null
            }
            val sender = when (userRole) {
                "company" -> "Работодатель"
                "admin" -> "Админ"
                else -> "Стажер"
            }

            val newMessage = Chat(
                idChat = 0,
                idUser =  userId ,
                textChat = inputText,
                statusChat = "Отправлено",
                sendAtChat = null,
                idVacancy = chat.idVacancy,
                idDirector = companyId,
                emailAdmin = if (isAdminChat) chat.emailAdmin else null,
                senderChat = sender
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

    // ========== ОБРАБОТЧИК КЛИКА ПО ИМЕНИ ==========
    fun handleNameClick() {
        when (userRole) {
            "admin" -> {
                if (isCompanyChat && chat.idDirector != null && chat.idDirector!! > 0) {
                    onNavigateToCompany(chat.idDirector!!)
                } else if (chat.idUser != null && chat.idUser!! > 0) {
                    onNavigateToUser(chat.idUser!!)
                }
            }
            "company" -> {
                if (chat.idUser != null && chat.idUser!! > 0) {
                    onNavigateToUser(chat.idUser!!)
                }
            }
            else -> { // student
                if (isCompanyChat && chat.idDirector != null && chat.idDirector!! > 0) {
                    onNavigateToCompany(chat.idDirector!!)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable { handleNameClick() }
                    ) {
                        Text(
                            displayName,
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
            if (isLoading && isFirstLoad) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Блок стажировки (только для компании)
                    if (userRole == "company" && !isAdminChat) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF5399BC).copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when {
                                    isLoadingInternship -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White
                                        )
                                    }
                                    internship == null -> {
                                        Button(
                                            onClick = { showStartDialog = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Начать стажировку", color = Color.White)
                                        }
                                    }
                                    internship?.statusIntership == "Закончена" -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                null,
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Стажировка завершена",
                                                color = Color(0xFF4CAF50),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    internship?.statusIntership == "Идет" -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Timer,
                                                    null,
                                                    tint = Color(0xFFFFB74D),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "Стажировка идет",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "Дней: $internshipDays",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = { endInternship() },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFFF9800)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                if (isEndingInternship) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(20.dp),
                                                        color = Color.White
                                                    )
                                                } else {
                                                    Icon(Icons.Default.Stop, null, tint = Color.White)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Завершить стажировку", color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Список сообщений
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages.reversed()) { msg ->
                            val isMe = when (userRole) {
                                "company" -> msg.chat.senderChat == "Работодатель"
                                "admin" -> msg.chat.senderChat == "Админ"
                                else -> msg.chat.senderChat == "Стажер"
                            }

                            MessageBubble(
                                message = msg.chat,
                                isMe = isMe
                            )
                        }
                    }

                    // Поле ввода
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

    if (showStartDialog) {
        AlertDialog(
            onDismissRequest = { showStartDialog = false },
            title = {
                Text(
                    "Начать стажировку",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Вы уверены, что хотите начать стажировку?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Стажировка начнется с сегодняшнего дня.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            },
            containerColor = Color(0xFF1E1E2E),
            confirmButton = {
                Button(
                    onClick = { startInternship() },
                    enabled = !isStartingInternship,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    if (isStartingInternship) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Начать", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStartDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Отмена", color = Color.White)
                }
            }
        )
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
                    if (!isMe) {
                        val senderName = when (message.senderChat) {
                            "Работодатель" -> "Работодатель"
                            "Администратор" -> "Администратор"
                            "Стажер" -> "Стажер"
                            "Админ" -> "Админ"
                            else -> ""
                        }
                        if (senderName.isNotEmpty()) {
                            Text(
                                senderName,
                                color = Color(0xFF5399BC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }

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
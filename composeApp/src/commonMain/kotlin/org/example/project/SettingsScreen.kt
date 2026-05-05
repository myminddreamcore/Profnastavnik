package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.API.ApiClient
import org.example.project.Models.CurrentUser
import org.example.project.Models.Student

@Composable
fun SettingsScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToFavourites: () -> Unit,
    onLogout: () -> Unit
) {
    var messageCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val user = Student(
            idStudent = CurrentUser.id ?: 0,
            emailStudent = CurrentUser.email ?: ""
        )
        val count = api.getCountUsermessages(user)
        if (count != null) {
            messageCount = count
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Настройки") { onNavigate(it) }
        },
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
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ПРОФНаставник",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        if (messageCount > 0) {
                            Surface(
                                color = Color.Red,
                                shape = CircleShape,
                                modifier = Modifier.size(12.dp)
                            ) {}
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Настройки",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsButton(
                        text = "Избранное",
                        icon = Icons.Default.Favorite,
                        onClick = onNavigateToFavourites
                    )

                    SettingsButton(
                        text = "Мои жалобы",
                        icon = Icons.Default.Report,
                        onClick = { /* TODO */ }
                    )

                    SettingsButton(
                        text = "Темная тема",
                        icon = Icons.Default.DarkMode,
                        onClick = { /* TODO */ }
                    )

                    SettingsButton(
                        text = "Чат-бот",
                        icon = Icons.Default.Chat,
                        onClick = { /* TODO */ }
                    )

                    SettingsButton(
                        text = "Мои отзывы",
                        icon = Icons.Default.RateReview,
                        onClick = { /* TODO */ }
                    )

                    SettingsButton(
                        text = "Архив стажировок",
                        icon = Icons.Default.Archive,
                        onClick = { /* TODO */ }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsButton(
                        text = "Удалить профиль",
                        icon = Icons.Default.DeleteForever,
                        onClick = { /* TODO */ }
                    )

                    SettingsButton(
                        text = "Выйти из аккаунта",
                        icon = Icons.Default.Logout,
                        onClick = onLogout
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SettingsButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() },
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.5f))
        }
    }
}
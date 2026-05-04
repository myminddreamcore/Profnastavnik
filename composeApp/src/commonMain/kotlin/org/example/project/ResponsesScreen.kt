package org.example.project
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.API.ApiClient
import org.example.project.Models.CurrentUser
import org.example.project.Models.Listresponcies
import org.example.project.Models.Student
import org.example.project.Models.UserVacancies
@Composable
fun ResponsesScreen(
    api: ApiClient,
    type: String,
    onBack: () -> Unit,
    onNavigateToDetail: () -> Unit
) {
    var responses by remember { mutableStateOf<List<Listresponcies>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val title = when(type) {
        "consider" -> "Рассматриваемые"
        "invited" -> "Приглашения"
        else -> "Отклоненные"
    }

    LaunchedEffect(Unit) {
        val user = Student(idStudent = CurrentUser.id ?: 0, emailStudent = CurrentUser.email ?: "")
        val result = when(type) {
            "consider" -> api.getconsiderVacancy(user)
            "invited" -> api.getinvitedVacancy(user)
            else -> api.getrejectedVacancy(user)
        }
        if (result != null) responses = result
        isLoading = false
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation("Дашборд") { if (it == "Дашборд") onBack() }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd))).padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(40.dp))
                Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp)

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(responses) { responseItem ->
                            ResponseDetailedCard(
                                item = responseItem,
                                onClick = { onNavigateToDetail() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResponseDetailedCard(item: Listresponcies, onClick: () -> Unit ={}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(item.nameVacancy ?: "Без названия", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${item.zenStart} - ${item.zenEnd} ${item.currency}", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Длительность: ${item.time}", color = Color(0xFF5399BC), fontSize = 12.sp)
        }
    }
}
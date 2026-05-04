package org.example.project
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
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
import org.example.project.API.MultiSelectField
import org.example.project.API.SimpleAutocompleteField
import org.example.project.Models.User

@Composable
fun EditProfileScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onSave: () -> Unit
) {
    var fio by remember { mutableStateOf("Башлыкова Дарья Александровна") }
    var birthDate by remember { mutableStateOf("14.01.2007") }
    var course by remember { mutableStateOf("3") }
    var specialty by remember { mutableStateOf("") }

    val allUniversities = listOf("Уксивт", "УГАТУ", "БГУ", "МГУ")
    val allSkills = listOf("Kotlin", "Java", "Compose", "SQL", "Git", "Figma")
    val allProfessions = listOf("Android Developer", "Backend Developer", "UI/UX Designer")

    val selectedSkills = remember { mutableStateListOf<String>() }
    val selectedProfs = remember { mutableStateListOf<String>() }
    var selectedUni by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Профиль") { target ->
                onNavigate(target)
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
            .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )  {
                Text("Редактирование профиля", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(modifier = Modifier.size(120.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.padding(20.dp))
                    }
                    IconButton(onClick = { }, modifier = Modifier.background(Color.White, CircleShape).size(32.dp)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomInputField("ФИО", fio) { fio = it }
                CustomInputField("Дата рождения", birthDate) { birthDate = it }

                SimpleAutocompleteField("Университет", allUniversities, selectedUni) { selectedUni = it }

                CustomInputField("Специальность", specialty) { specialty = it }
                CustomInputField("Курс", course) { course = it }

                MultiSelectField("Навыки", allSkills, selectedSkills, "Введите навык...")
                MultiSelectField("Желаемые профессии", allProfessions, selectedProfs, "Введите профессию...")

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.UploadFile, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Загрузить портфолио (PDF)")
                }

                Button(
                    onClick = { onSave() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC))
                ) {
                    Text("Сохранить", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CustomInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            )
        )
    }
}

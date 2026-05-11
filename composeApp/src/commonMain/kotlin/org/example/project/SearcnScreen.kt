package org.example.project

import org.example.project.Models.CurrentUser
import org.example.project.Models.Student
import org.example.project.Models.Filters
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import org.example.project.Models.Listresponcies
import org.example.project.Models.Skills
import org.example.project.Models.Sferes
import org.example.project.Models.Formats
import org.example.project.API.MultiSelectFieldFromApi

@Composable
fun SearchScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToChats: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var messageCount by remember { mutableStateOf(0) }
    var showFilters by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Listresponcies>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    var selectedSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSpheres by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFormats by remember { mutableStateOf<List<String>>(emptyList()) }
    var minSalary by remember { mutableStateOf("") }
    var maxSalary by remember { mutableStateOf("") }
    var hasMentor by remember { mutableStateOf(false) }
    var allSkills by remember { mutableStateOf<List<Skills>>(emptyList()) }
    var allSpheres by remember { mutableStateOf<List<Sferes>>(emptyList()) }
    var allFormats by remember { mutableStateOf<List<Formats>>(emptyList()) }

    LaunchedEffect(Unit) {
        val user = Student(
            idStudent = CurrentUser.id ?: 0,
            emailStudent = CurrentUser.email ?: ""
        )
        val count = api.getCountUsermessages(user)
        if (count != null) messageCount = count

        val skills = api.getSkills()
        if (skills != null) allSkills = skills

        val spheres = api.getSferes()
        if (spheres != null) allSpheres = spheres

        val formats = api.getFormats()
        if (formats != null) allFormats = formats
    }

    val skillNames = allSkills.map { it.nameSkill ?: "" }
    val sphereNames = allSpheres.map { it.nameSfere ?: "" }
    val formatNames = allFormats.map { it.nameVacancyFormat ?: "" }

    fun performSearch() {
        scope.launch {
            isLoading = true
            hasSearched = true

            val filters = Filters(
                skills = if (selectedSkills.isNotEmpty()) selectedSkills else null,
                sferes = if (selectedSpheres.isNotEmpty()) selectedSpheres else null,
                formats = if (selectedFormats.isNotEmpty()) selectedFormats else null,
                zenstart = minSalary.toIntOrNull(),
                zenEnd = maxSalary.toIntOrNull(),
                user = if (hasMentor) true else null,
                name = if (searchQuery.isNotBlank()) searchQuery else null
            )

            val results = api.getfilteredVacancy(filters)
            searchResults = results ?: emptyList()
            isLoading = false
        }
    }

    fun resetFilters() {
        selectedSkills = emptyList()
        selectedSpheres = emptyList()
        selectedFormats = emptyList()
        minSalary = ""
        maxSalary = ""
        hasMentor = false
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Поиск") { onNavigate(it) }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Box(
                        modifier = Modifier.clickable { onNavigateToChats() },
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            "Уведомления",
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

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(
                            Icons.Default.Tune,
                            "Фильтры",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        placeholder = { Text("Поиск вакансий...", color = Color.White.copy(alpha = 0.6f)) },
                        shape = RoundedCornerShape(27.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(onClick = { performSearch() }) {
                                Icon(Icons.Default.Search, null, tint = Color.White)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Найденные вакансии:", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(16.dp))

                        when {
                            isLoading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            }
                            !hasSearched -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Введите запрос для поиска", color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                            searchResults.isEmpty() -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.SearchOff, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Ничего не найдено", color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                            else -> {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    items(searchResults) { vacancy ->
                                        SearchResultCard(
                                            item = vacancy,
                                            onClick = { onNavigateToDetail(vacancy.idVacancy ?: 0) }
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

    if (showFilters) {
        AlertDialog(
            onDismissRequest = { showFilters = false },
            title = { Text("Фильтры", color = Color.White, fontWeight = FontWeight.Bold) },
            containerColor = Color(0xFF1E1E2E),
            modifier = Modifier.fillMaxWidth(0.95f),
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    MultiSelectFieldFromApi(
                        label = "Навыки",
                        options = skillNames,
                        selectedOptionsNames = selectedSkills,
                        onOptionsSelected = { newList ->
                            selectedSkills = newList
                        },
                        placeholder = "Введите навык..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiSelectFieldFromApi(
                        label = "Сферы деятельности",
                        options = sphereNames,
                        selectedOptionsNames = selectedSpheres,
                        onOptionsSelected = { newList ->
                            selectedSpheres = newList
                        },
                        placeholder = "Введите сферу..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiSelectFieldFromApi(
                        label = "Формат работы",
                        options = formatNames,
                        selectedOptionsNames = selectedFormats,
                        onOptionsSelected = { newList ->
                            selectedFormats = newList
                        },
                        placeholder = "Введите формат..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Зарплата", color = Color.White, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minSalary,
                            onValueChange = { minSalary = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("От", color = Color.White.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF5399BC),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        OutlinedTextField(
                            value = maxSalary,
                            onValueChange = { maxSalary = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("До", color = Color.White.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF5399BC),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hasMentor,
                            onCheckedChange = { hasMentor = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5399BC))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("С наставником", color = Color.White)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFilters = false
                        performSearch()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC))
                ) {
                    Text("Применить")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        resetFilters()
                    }
                ) {
                    Text("Сбросить")
                }
            }
        )
    }
}

@Composable
fun SearchResultCard(item: Listresponcies, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.nameVacancy ?: "Вакансия", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${item.zenStart} - ${item.zenEnd} ${item.currency}", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Длительность: ${item.time}", color = Color(0xFF5399BC), fontSize = 12.sp)
        }
    }
}
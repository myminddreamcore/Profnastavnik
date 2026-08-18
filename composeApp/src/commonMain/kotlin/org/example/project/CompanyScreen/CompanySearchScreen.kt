package org.example.project.CompanyScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.Models.Skills
import org.example.project.Models.University
import org.example.project.Models.Proffesions
import org.example.project.API.MultiSelectFieldFromApi
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.StudentSearchFilters
import org.example.project.Models.StudentSearchResult
import org.example.project.Models.CurrentUser
import org.example.project.UserScreen.CustomBottomNavigation

@Composable
fun CompanySearchScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToTariffs: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<StudentSearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    // Состояние тарифа
    var isTariffPro by remember { mutableStateOf(false) }
    var isLoadingTariff by remember { mutableStateOf(true) }

    // Фильтры для студентов
    var selectedSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedProfessions by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedUniversity by remember { mutableStateOf<String?>(null) }
    var minCourse by remember { mutableStateOf("") }
    var maxCourse by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    // Доступные опции для фильтров
    var allSkills by remember { mutableStateOf<List<Skills>>(emptyList()) }
    var allProfessions by remember { mutableStateOf<List<Proffesions>>(emptyList()) }
    var allUniversities by remember { mutableStateOf<List<University>>(emptyList()) }

    // Загрузка данных
    LaunchedEffect(Unit) {
        val companyId = CurrentUser.id ?: 0

        // Загружаем справочники
        val skills = api.getSkills()
        if (skills != null) allSkills = skills

        val profs = api.getProffesions()
        if (profs != null) allProfessions = profs

        val unis = api.getUniversity()
        if (unis != null) allUniversities = unis

        // Загружаем тариф компании
        val tariffId = api.getCompanyTariff(companyId)
        // Тариф с id = 3 - это Корпоративный (доступ к поиску)
        isTariffPro = tariffId?.idPrice == 3
        isLoadingTariff = false
    }

    val skillNames = allSkills.map { it.nameSkill ?: "" }
    val professionNames = allProfessions.map { it.nameProfession ?: "" }
    val universityNames = allUniversities.map { it.nameUniversity ?: "" }

    fun performSearch() {
        if (!isTariffPro) return

        scope.launch {
            isLoading = true
            hasSearched = true

            val filters = StudentSearchFilters(
                fullName = searchQuery.takeIf { it.isNotBlank() },
                skills = selectedSkills.takeIf { it.isNotEmpty() },
                professions = selectedProfessions.takeIf { it.isNotEmpty() },
                university = selectedUniversity,
                minCourse = minCourse.toIntOrNull(),
                maxCourse = maxCourse.toIntOrNull()
            )

            val results = api.searchStudents(filters)
            searchResults = results ?: emptyList()
            isLoading = false
        }
    }

    fun resetFilters() {
        selectedSkills = emptyList()
        selectedProfessions = emptyList()
        selectedUniversity = null
        minCourse = ""
        maxCourse = ""
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Поиск") { target ->
                onNavigate(target)
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
                .padding(padding)
        ) {
            if (isLoadingTariff) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Загрузка...", color = Color.White)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Верхняя панель
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isTariffPro) "Поиск студентов" else "Поиск студентов (Pro)",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(modifier = Modifier.size(40.dp))
                    }

                    // Если не Профи тариф - показываем баннер
                    if (!isTariffPro) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFB74D).copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    null,
                                    tint = Color(0xFFFFB74D),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Доступ к поиску студентов открыт в тарифе Корпоративный",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Подключите тариф Корпоративный, чтобы находить лучших студентов",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onNavigateToTariffs() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5399BC)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Перейти к тарифам", color = Color.White)
                                }
                            }
                        }
                    } else {
                        // Строка поиска
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { showFilters = true }) {
                                Icon(
                                    Icons.Default.Tune,
                                    "Фильтры",
                                    tint =  Color.White,
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
                                placeholder = { Text("Поиск студентов...", color = Color.White.copy(alpha = 0.6f)) },
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

                        // Результаты поиска
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    "Найденные студенты:",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
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
                                            items(searchResults) { student ->
                                                StudentResultCard(
                                                    student = student,
                                                    onClick = { onNavigateToUserProfile(student.idStudent) }
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
        }
    }

    // Диалог фильтров (только для Профи тарифа)
    if (showFilters && isTariffPro) {
        AlertDialog(
            onDismissRequest = { showFilters = false },
            title = { Text("Фильтры студентов", color = Color.White, fontWeight = FontWeight.Bold) },
            containerColor = Color(0xFF1E1E2E),
            modifier = Modifier.fillMaxWidth(0.95f),
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    // Поиск по имени
                    Text("Поиск по ФИО", color = Color.White, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Введите имя или фамилию", color = Color.White.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF5399BC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Навыки
                    MultiSelectFieldFromApi(
                        label = "Навыки",
                        options = skillNames,
                        selectedOptionsNames = selectedSkills,
                        onOptionsSelected = { selectedSkills = it },
                        placeholder = "Введите навык..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Профессии
                    MultiSelectFieldFromApi(
                        label = "Желаемые профессии",
                        options = professionNames,
                        selectedOptionsNames = selectedProfessions,
                        onOptionsSelected = { selectedProfessions = it },
                        placeholder = "Введите профессию..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Университет
                    UniversityAutocompleteField(
                        label = "Университет",
                        options = universityNames,
                        selectedOption = selectedUniversity ?: "",
                        onOptionSelected = { selectedUniversity = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Курс
                    Text("Курс", color = Color.White, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minCourse,
                            onValueChange = { minCourse = it },
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
                            value = maxCourse,
                            onValueChange = { maxCourse = it },
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
fun StudentResultCard(student: StudentSearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                student.fullName ?: "Студент",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                student.emailStudent ?: "",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )

            if (student.courseStudent != null) {
                Text(
                    "Курс: ${student.courseStudent}",
                    color = Color(0xFF5399BC),
                    fontSize = 13.sp
                )
            }

            if (!student.facultatyStudent.isNullOrBlank()) {
                Text(
                    student.facultatyStudent ?: "",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            if (student.universityStudent != null) {
                Text(
                    "Университет: ${student.universityStudent}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            // Навыки
            if (!student.skills.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    student.skills?.take(5)?.forEach { skill ->
                        Surface(
                            color = Color(0xFF5399BC).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                skill,
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if ((student.skills?.size ?: 0) > 5) {
                        Text(
                            "+${(student.skills?.size ?: 0) - 5}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityAutocompleteField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf(selectedOption) }

    val filteredOptions = if (inputText.isNotEmpty()) {
        options.filter { it.contains(inputText, ignoreCase = true) }
    } else {
        options
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    expanded = true
                    if (it.isEmpty()) onOptionSelected("")
                },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                placeholder = { Text("Выберите университет", color = Color.White.copy(alpha = 0.5f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded && filteredOptions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF2D3243))
            ) {
                filteredOptions.take(10).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.White, fontSize = 14.sp) },
                        onClick = {
                            inputText = option
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
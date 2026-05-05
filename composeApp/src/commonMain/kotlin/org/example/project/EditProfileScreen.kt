package org.example.project
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.API.ApiClient
import org.example.project.API.MultiSelectFieldFromApi
import org.example.project.Models.Proffesions
import org.example.project.Models.Skills
import org.example.project.Models.University
import org.example.project.Models.CurrentUser

@Composable
fun EditProfileScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onSave: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var fio by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }

    var universities by remember { mutableStateOf<List<University>>(emptyList()) }
    var allSkills by remember { mutableStateOf<List<Skills>>(emptyList()) }
    var allProfessions by remember { mutableStateOf<List<Proffesions>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }

    var selectedUniversity by remember { mutableStateOf<University?>(null) }
    val selectedSkills = remember { mutableStateListOf<Skills>() }
    val selectedProfessions = remember { mutableStateListOf<Proffesions>() }

    LaunchedEffect(Unit) {
        isLoading = true
        val unis = api.getUniversity()
        val skills = api.getSkills()
        val profs = api.getProffesions()

        if (unis != null) universities = unis
        if (skills != null) allSkills = skills
        if (profs != null) allProfessions = profs

        // TODO: Загрузить данные текущего пользователя через api.getUserProfile()

        isLoading = false
    }

    val universityNames = universities.map { it.nameUniversity ?: "" }
    val skillNames = allSkills.map { it.nameSkill ?: "" }
    val professionNames = allProfessions.map { it.nameProfession ?: "" }

    fun findUniversityByName(name: String): University? = universities.find { it.nameUniversity == name }
    fun findSkillByName(name: String): Skills? = allSkills.find { it.nameSkill == name }
    fun findProfessionByName(name: String): Proffesions? = allProfessions.find { it.nameProfession == name }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Профиль") { target ->
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Редактирование профиля",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                tint = Color.White,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { /* TODO: Загрузка фото */ },
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomInputField("ФИО", fio) { fio = it }
                    CustomInputField("Дата рождения", birthDate) { birthDate = it }

                    UniversityAutocompleteField(
                        label = "Университет",
                        options = universityNames,
                        selectedOption = selectedUniversity?.nameUniversity ?: "",
                        onOptionSelected = { selectedUniversity = findUniversityByName(it) }
                    )

                    CustomInputField("Специальность", specialty) { specialty = it }
                    CustomInputField("Курс", course) { course = it }

                    MultiSelectFieldFromApi(
                        label = "Навыки",
                        options = skillNames,
                        selectedOptionsNames = selectedSkills.map { it.nameSkill ?: "" }.toMutableList(),
                        onOptionsSelected = { selectedNames ->
                            selectedSkills.clear()
                            selectedNames.forEach { name ->
                                findSkillByName(name)?.let { selectedSkills.add(it) }
                            }
                        },
                        placeholder = "Введите навык..."
                    )

                    MultiSelectFieldFromApi(
                        label = "Желаемые профессии",
                        options = professionNames,
                        selectedOptionsNames = selectedProfessions.map { it.nameProfession ?: "" }.toMutableList(),
                        onOptionsSelected = { selectedNames ->
                            selectedProfessions.clear()
                            selectedNames.forEach { name ->
                                findProfessionByName(name)?.let { selectedProfessions.add(it) }
                            }
                        },
                        placeholder = "Введите профессию..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        text = "Загрузить портфолио (PDF)",
                        icon = Icons.Default.UploadFile,
                        onClick = { /* TODO: Загрузка PDF */ },
                        gradient = listOf(Color(0xFF4A90E2), Color(0xFF7B61FF))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        text = "Сохранить изменения",
                        icon = Icons.Default.Save,
                        onClick = {
                            scope.launch {
                                // TODO: Отправить данные на сервер через api.updateProfile()
                                onSave()
                            }
                        },
                        gradient = listOf(Color(0xFF5399BC), Color(0xFF7B61FF))
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun CustomInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
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
                        text = {
                            Text(
                                option,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        },
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



@Composable
fun GradientButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    gradient: List<Color>
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(gradient),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
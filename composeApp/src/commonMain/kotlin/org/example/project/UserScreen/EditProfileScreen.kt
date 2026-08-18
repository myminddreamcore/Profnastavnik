package org.example.project.UserScreen

import androidx.compose.foundation.background
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
import org.example.project.Models.Proffesions
import org.example.project.Models.Skills
import org.example.project.Models.University
import org.example.project.Models.CurrentUser
import org.example.project.Models.AllUserprofile
import org.example.project.API.MultiSelectFieldFromApi
import org.example.project.BgGradientEnd
import org.example.project.BgGradientStart
import org.example.project.Models.Student

@Composable
fun EditProfileScreen(
    api: ApiClient,
    onNavigate: (String) -> Unit,
    onSave: () -> Unit,

) {
    val scope = rememberCoroutineScope()

    var fio by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }

    var universities by remember { mutableStateOf<List<University>>(emptyList()) }
    var allSkills by remember { mutableStateOf<List<Skills>>(emptyList()) }
    var allProfessions by remember { mutableStateOf<List<Proffesions>>(emptyList()) }

    var selectedUniversity by remember { mutableStateOf<University?>(null) }
    val selectedSkills = remember { mutableStateListOf<Skills>() }
    val selectedProfessions = remember { mutableStateListOf<Proffesions>() }

    var userProfile by remember { mutableStateOf<AllUserprofile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val userId = CurrentUser.id ?: 0

            val unis = api.getUniversity()
            if (unis != null) universities = unis

            val skills = api.getSkills()
            if (skills != null) allSkills = skills

            val profs = api.getProffesions()
            if (profs != null) allProfessions = profs

            val profile = api.getAllUserProfile(userId)
            if (profile != null) {
                userProfile = profile

                profile.student?.let { student ->
                    val name = student.nameStudent ?: ""
                    val surname = student.surnameStudent ?: ""
                    val patronymic = student.patronymicStudent ?: ""
                    fio = listOf(surname, name, patronymic).filter { it.isNotEmpty() }.joinToString(" ")
                    birthDate = student.birthdayStudent ?: ""
                    course = student.courseStudent?.toString() ?: ""
                    specialty = student.facultatyStudent ?: ""
                    email = student.emailStudent ?: ""
                }

                profile.university?.let { uniName ->
                    selectedUniversity = universities.find {
                        it.nameUniversity?.trim()?.equals(uniName.trim(), ignoreCase = true) == true
                    }
                }

                profile.skills?.forEach { skillName ->
                    allSkills.find {
                        it.nameSkill?.trim()?.equals(skillName.trim(), ignoreCase = true) == true
                    }?.let { selectedSkills.add(it) }
                }

                profile.proffesions?.forEach { profName ->
                    allProfessions.find {
                        it.nameProfession?.trim()?.equals(profName.trim(), ignoreCase = true) == true
                    }?.let { selectedProfessions.add(it) }
                }
            } else {
                errorMessage = "Не удалось загрузить профиль"
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
        isLoading = false
    }

    val universityNames = universities.map { it.nameUniversity ?: "" }
    val skillNames = allSkills.map { it.nameSkill ?: "" }
    val professionNames = allProfessions.map { it.nameProfession ?: "" }

    fun findUniversityByName(name: String) = universities.find { it.nameUniversity == name }
    fun findSkillByName(name: String) = allSkills.find { it.nameSkill == name }
    fun findProfessionByName(name: String) = allProfessions.find { it.nameProfession == name }

    val snackbarHostState = remember { SnackbarHostState() }
    // Функция сохранения профиля - теперь внутри composable
    fun saveProfile() {
        scope.launch {
            try {
                val nameParts = fio.split(" ")
                val surname = nameParts.getOrNull(0) ?: ""
                val name = nameParts.getOrNull(1) ?: ""
                val patronymic = nameParts.getOrNull(2) ?: ""

                val profileToSave = AllUserprofile(
                    student = Student(
                        idStudent = userProfile?.student!!.idStudent,
                        nameStudent = if (name.isNotEmpty()) name else null,
                        surnameStudent = if (surname.isNotEmpty()) surname else null,
                        patronymicStudent = if (patronymic.isNotEmpty()) patronymic else null,
                        birthdayStudent = if (birthDate.isNotEmpty()) birthDate else null,
                        courseStudent = course.toIntOrNull(),
                        facultatyStudent = if (specialty.isNotEmpty()) specialty else null,
                        emailStudent = userProfile?.student!!.emailStudent,
                        universityStudent = selectedUniversity?.idUniversity
                    ),
                    university = selectedUniversity?.nameUniversity ?: "",
                    skills = selectedSkills.map { it.nameSkill ?: "" },
                    proffesions = selectedProfessions.map { it.nameProfession ?: "" },
                    studentFiles = userProfile?.studentFiles ?: emptyList()
                )

                val success = api.saveUserProfile(profileToSave)
                println(profileToSave)
                if (success) {
                    snackbarHostState.showSnackbar("Профиль успешно сохранен!")
                    onSave()
                } else {
                    snackbarHostState.showSnackbar("Ошибка при сохранении профиля")
                    println("Ошибка при сохранении профиля")
                }
            } catch (e: Exception) {
                println("Ошибка: ${e.message}")
            }
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(currentScreen = "Профиль") { target ->
                onNavigate(target)
            }
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Ошибка", color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Редактирование профиля", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Аватар
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
                                modifier = Modifier.fillMaxSize().padding(20.dp)
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

                    CustomInputField("ФИО", fio ,{ fio = it }, enabled = true)
                    CustomInputField("Логин/почта", email, { email = it },enabled = false)
                    CustomInputField("Дата рождения", birthDate, { birthDate = it } , enabled = false)

                    UniversityAutocompleteField(
                        label = "Университет",
                        options = universityNames,
                        selectedOption = selectedUniversity?.nameUniversity ?: "",
                        onOptionSelected = { selectedUniversity = findUniversityByName(it) }
                    )

                    CustomInputField("Специальность", specialty, { specialty = it }, enabled = true)
                    CustomInputField("Курс", course,{ course = it },enabled = true )

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
                        onClick = { saveProfile() }, // Вызываем функцию сохранения
                        gradient = listOf(Color(0xFF5399BC), Color(0xFF7B61FF))
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true  // Добавляем параметр
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,  // Используем параметр
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledTextColor = Color.White.copy(alpha = 0.5f),  // Цвет текста для disabled
                disabledBorderColor = Color.White.copy(alpha = 0.3f)  // Цвет рамки для disabled
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

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    gradient: List<Color>
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        content = {
            Box(
                modifier = Modifier.fillMaxSize().background(
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
                        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
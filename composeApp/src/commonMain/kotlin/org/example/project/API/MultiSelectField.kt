package org.example.project.API

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiSelectFieldFromApi(
    label: String,
    options: List<String>,
    selectedOptionsNames: List<String>,
    onOptionsSelected: (List<String>) -> Unit,
    placeholder: String
) {
    var text by remember { mutableStateOf("") }
    var selectedLocal by remember(selectedOptionsNames) { mutableStateOf(selectedOptionsNames) }

    LaunchedEffect(selectedOptionsNames) {
        if (selectedLocal != selectedOptionsNames) {
            selectedLocal = selectedOptionsNames
        }
    }

    val filteredOptions = options.filter {
        it.contains(text, ignoreCase = true) && it !in selectedLocal
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selectedLocal.forEach { item ->
                AssistChip(
                    onClick = {
                        val newList = selectedLocal.toMutableList().apply { remove(item) }
                        selectedLocal = newList
                        onOptionsSelected(newList)
                    },
                    label = {
                        Text(item, color = Color.White, fontSize = 12.sp)
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Удалить",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF5399BC).copy(alpha = 0.8f)
                    )
                )
            }
        }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        if (text.isNotEmpty() && filteredOptions.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3243)),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp)
                    .padding(top = 4.dp)
            ) {
                LazyColumn {
                    items(filteredOptions.take(8)) { option ->
                        Text(
                            text = option,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newList = selectedLocal.toMutableList().apply { add(option) }
                                    selectedLocal = newList
                                    onOptionsSelected(newList)
                                    text = ""
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
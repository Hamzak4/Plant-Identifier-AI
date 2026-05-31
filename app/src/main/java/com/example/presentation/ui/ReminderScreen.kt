package com.example.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReminderEntity
import com.example.presentation.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    reminderViewModel: ReminderViewModel,
    onNavigateBack: () -> Unit
) {
    val reminderItems by reminderViewModel.reminders.collectAsState()

    var plantName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Water Plant") }
    var intervalDaysString by remember { mutableStateOf("7") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Care Reminders scheduler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add reminders card panel
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Schedule Care Alarm",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = plantName,
                                onValueChange = { plantName = it },
                                label = { Text("Plant Name (e.g. Aloe Vera)") },
                                modifier = Modifier.fillMaxWidth().testTag("reminder_plant_input"),
                                singleLine = true
                            )

                            Text("Care Event Task", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Water Plant", "Fertilizer", "Pruning").forEach { type ->
                                    val isSelected = selectedType == type
                                    Button(
                                        onClick = { selectedType = type },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Text(type.split(" ").first(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = intervalDaysString,
                                onValueChange = { intervalDaysString = it.filter { char -> char.isDigit() } },
                                label = { Text("Repeat Interval (Days)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("reminder_days_input"),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    val days = intervalDaysString.toIntOrNull() ?: 7
                                    if (plantName.isNotBlank()) {
                                        reminderViewModel.addReminder(plantName, selectedType, days)
                                        plantName = ""
                                    }
                                },
                                enabled = plantName.isNotBlank() && intervalDaysString.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("reminder_submit_btn")
                            ) {
                                Icon(Icons.Default.AddAlarm, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Scheduling Alarm", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Header log lists
                item {
                    Text(
                        text = "Active Reminders Calendars",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (reminderItems.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No calendar scheduling active.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(reminderItems, key = { it.id }) { alarm ->
                        AlarmRowComponent(
                            alarm = alarm,
                            onMarkDone = { reminderViewModel.completeReminderTask(alarm) },
                            onToggle = { reminderViewModel.toggleReminder(alarm) },
                            onDelete = { reminderViewModel.deleteReminder(alarm) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmRowComponent(
    alarm: ReminderEntity,
    onMarkDone: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (alarm.type) {
                            "Water Plant" -> Icons.Default.WaterDrop
                            "Fertilizer" -> Icons.Default.Eco
                            else -> Icons.Default.ContentCut
                        },
                        contentDescription = null,
                        tint = when (alarm.type) {
                            "Water Plant" -> MaterialTheme.colorScheme.primary
                            "Fertilizer" -> Color(0xFF2E7D32)
                            else -> Color(0xFF795548)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(alarm.plantName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${alarm.type} • Every ${alarm.intervalDays} Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = alarm.isEnabled,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier.scale(0.8f)
                    )
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete alarm", tint = Color.Red.copy(alpha = 0.6f))
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formattedDueDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(alarm.nextDueDate))
                Column {
                    Text("NEXT DUE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(formattedDueDate, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }

                if (alarm.isEnabled) {
                    Button(
                        onClick = onMarkDone,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Done", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

package com.example.presentation.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.EssayEntity
import com.example.presentation.viewmodel.EssayUiState
import com.example.presentation.viewmodel.EssayViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssayScreen(
    essayViewModel: EssayViewModel
) {
    val context = LocalContext.current
    val savedEssays by essayViewModel.savedEssays.collectAsState()
    val essayState by essayViewModel.essayContentState.collectAsState()

    var topic by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English") }
    var wordCount by remember { mutableStateOf(400f) }
    var selectedEducation by remember { mutableStateOf("University") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Scientific Essay Writer", fontWeight = FontWeight.Bold) },
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
                // Feature Header
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Automated Botanical Papers & Essays",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Instantly write complete plant descriptions or compositions in English/Urdu with Gemini AI.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Configurations block
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Compositions Parameters",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = topic,
                                onValueChange = { topic = it },
                                label = { Text("Essay Topic (e.g. Medicinal Benefits of Neem)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("essay_topic_input"),
                                singleLine = true
                            )

                            // Language segmented
                            Text("Language", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("English", "Urdu").forEach { lang ->
                                    val isSelected = language == lang
                                    Button(
                                        onClick = { language = lang },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text(lang, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Education Level segmented
                            Text("Target Academic Level", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("School", "College", "University").forEach { edu ->
                                    val isSelected = selectedEducation == edu
                                    Button(
                                        onClick = { selectedEducation = edu },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text(edu, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Word slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Length: ${wordCount.toInt()} words", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = wordCount,
                                onValueChange = { wordCount = it },
                                valueRange = 100f..1000f,
                                steps = 9,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (essayState is EssayUiState.Loading) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Drafting botanical paper...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (topic.isNotBlank()) {
                                            essayViewModel.generateEssay(
                                                topic = topic,
                                                language = language,
                                                wordCount = wordCount.toInt(),
                                                educationLevel = selectedEducation
                                            )
                                        }
                                    },
                                    enabled = topic.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("essay_generate_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.BorderColor, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Draft Scientific Compositions", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // AI Outflow result
                if (essayState is EssayUiState.Success) {
                    val content = (essayState as EssayUiState.Success).content
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().animateContentSize()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Generated Compositions",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Row {
                                        // Save
                                        IconButton(
                                            onClick = {
                                                essayViewModel.saveEssay(
                                                    topic = topic,
                                                    content = content,
                                                    language = language,
                                                    wordCount = wordCount.toInt(),
                                                    eduLevel = selectedEducation
                                                )
                                                Toast.makeText(context, "Essay saved offline! 🌱", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Default.BookmarkAdd, contentDescription = "Save Essay")
                                        }

                                        // Copy
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("AI Essay", content)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy text")
                                        }

                                        // Share
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TITLE, topic)
                                                    putExtra(Intent.EXTRA_TEXT, content)
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Share Scientific Essay"))
                                            }
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share text")
                                        }

                                        // Regenerate
                                        IconButton(
                                            onClick = {
                                                essayViewModel.generateEssay(
                                                    topic = topic,
                                                    language = language,
                                                    wordCount = wordCount.toInt(),
                                                    educationLevel = selectedEducation
                                                )
                                            }
                                        ) {
                                            Icon(Icons.Default.Loop, contentDescription = "Regenerate")
                                        }
                                    }
                                }

                                Divider()

                                Text(
                                    text = content,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = if (language == "Urdu") TextAlign.Right else TextAlign.Left
                                )
                            }
                        }
                    }
                }

                if (essayState is EssayUiState.Error) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (essayState as EssayUiState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Saved logs
                item {
                    Text(
                        text = "Saved Local Papers History",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (savedEssays.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No compositions saved. Tap 'Bookmark' above to save results.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(savedEssays, key = { it.id }) { essay ->
                        SavedEssayRow(
                            essay = essay,
                            onDelete = { essayViewModel.deleteEssay(essay) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedEssayRow(
    essay: EssayEntity,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.animateContentSize()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = essay.topic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(essay.language, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(essay.createdAt))
                        Text(formattedDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }

                Row {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Saved AI Essay", essay.content)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Essay Log",
                            tint = Color.Red.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = essay.content,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (essay.language == "Urdu") TextAlign.Right else TextAlign.Left
                )
            }
        }
    }
}

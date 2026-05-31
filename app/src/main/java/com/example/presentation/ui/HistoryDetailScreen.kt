package com.example.presentation.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlantEntity
import com.example.presentation.viewmodel.HistoryViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    plant: PlantEntity,
    historyViewModel: HistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val tags = remember(plant.name, plant.description) {
        getPlantCategoriesFromDetail(plant.name, plant.description, plant.family)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal Entry", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.testTag("delete_detail_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete from history",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Scrollable content area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Plant Image Preview: rounded-3xl, 4/3 aspect, elegant border shadow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .aspectRatio(1.33f) // 4:3 aspect ratio
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        val file = File(plant.imageUri)
                        AsyncImage(
                            model = if (file.exists()) file else plant.imageUri,
                            contentDescription = "Saved Plant Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Soft elegant shadow overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                        startY = 150f
                                    )
                                )
                        )

                        // Confidence Score Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${plant.confidence.toInt()}% Match",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Info Sheet Representation (rounded 32dp card overlay)
                    var displayLanguage by remember { mutableStateOf("English") }

                    Card(
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            // Language Switcher tab bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { displayLanguage = "English" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (displayLanguage == "English") MaterialTheme.colorScheme.surface else Color.Transparent,
                                        contentColor = if (displayLanguage == "English") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = if (displayLanguage == "English") ButtonDefaults.buttonElevation(defaultElevation = 1.dp) else null,
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 0.dp)
                                ) {
                                    Text("English Guide", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { displayLanguage = "Urdu" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (displayLanguage == "Urdu") MaterialTheme.colorScheme.surface else Color.Transparent,
                                        contentColor = if (displayLanguage == "Urdu") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = if (displayLanguage == "Urdu") ButtonDefaults.buttonElevation(defaultElevation = 1.dp) else null,
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 0.dp)
                                ) {
                                    Text("اردو گائیڈ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            val isUrdu = displayLanguage == "Urdu"
                            val displayName = if (isUrdu && plant.nameUrdu.isNotBlank()) plant.nameUrdu else plant.name
                            val displayFamily = if (isUrdu && plant.familyUrdu.isNotBlank()) plant.familyUrdu else plant.family
                            val displayDesc = if (isUrdu && plant.descriptionUrdu.isNotBlank()) plant.descriptionUrdu else plant.description
                            val displayCare = if (isUrdu && plant.careInstructionsUrdu.isNotBlank()) plant.careInstructionsUrdu else plant.careInstructions
                            val displayWater = if (isUrdu && plant.waterRequirementsUrdu.isNotBlank()) plant.waterRequirementsUrdu else plant.waterRequirements
                            val displaySun = if (isUrdu && plant.sunlightRequirementsUrdu.isNotBlank()) plant.sunlightRequirementsUrdu else plant.sunlightRequirements

                            // Title with name and a green florist icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = displayName,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 28.sp,
                                        textAlign = if (isUrdu) TextAlign.Right else TextAlign.Left,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = plant.scientificName,
                                        fontSize = 15.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(8.dp)
                                    ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = "Family indicator",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isUrdu) "خاندان: $displayFamily" else "$displayFamily family",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 2.dp).fillMaxWidth(),
                                textAlign = if (isUrdu) TextAlign.Right else TextAlign.Left
                            )

                            // Journal scan calendar date tagline
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = if (isUrdu) Arrangement.End else Arrangement.Start
                            ) {
                                if (isUrdu) {
                                    val formattedDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(plant.dateIdentified))
                                    Text(
                                        text = "$formattedDate کو شناخت کیا گیا",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Scan Date",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Scan Date",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val formattedDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(plant.dateIdentified))
                                    Text(
                                        text = "Identified on $formattedDate",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Dynamic tags
                            Row(
                                horizontalArrangement = if (isUrdu) Arrangement.End else Arrangement.Start,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp)
                            ) {
                                for (tag in tags) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            // Watering & Sunlight Range Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HistoryQuickCareItem(
                                    modifier = Modifier.weight(1f),
                                    emoji = "💧",
                                    label = if (isUrdu) "پانی کی ضرورت" else "Watering",
                                    value = displayWater
                                )
                                HistoryQuickCareItem(
                                    modifier = Modifier.weight(1f),
                                    emoji = "☀️",
                                    label = if (isUrdu) "سورج کی روشنی" else "Sunlight",
                                    value = displaySun
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Clean description layout
                            Text(
                                text = if (isUrdu) "تفصیل" else "DESCRIPTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 1.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = if (isUrdu) TextAlign.Right else TextAlign.Left
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = displayDesc,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = if (isUrdu) TextAlign.Right else TextAlign.Left
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Horticultural Care Instructions
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (isUrdu) Arrangement.End else Arrangement.Start
                                    ) {
                                        if (isUrdu) {
                                            Text(
                                                text = "دیکھ بھال کی ہدایات",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Expert Care Instructions",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = displayCare,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = if (isUrdu) TextAlign.Right else TextAlign.Left
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom actions: Share Button matching Professional Polish
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = { sharePlantDetailsFromEntity(context, plant) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("share_detail_item_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Profile icon", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Plant Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Confirm deletion prompt
            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Delete Log Entry", fontWeight = FontWeight.Bold) },
                    text = { Text("Are you sure you want to delete this specific search snapshot from your garden journal?") },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Cancel")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                historyViewModel.deletePlant(plant)
                                showDeleteConfirm = false
                                onNavigateBack()
                            },
                            modifier = Modifier.testTag("confirm_delete_detail_btn")
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("delete_detail_dialog")
                )
            }
        }
    }
}

@Composable
fun HistoryQuickCareItem(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Column {
                Text(
                    text = label.uppercase(Locale.getDefault()),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun getPlantCategoriesFromDetail(plantName: String, description: String, family: String): List<String> {
    val tags = mutableListOf<String>()
    val descLower = description.lowercase()
    val nameLower = plantName.lowercase()
    
    if (descLower.contains("indoor") || descLower.contains("houseplant") || descLower.contains("pot") || nameLower.contains("monstera")) {
        tags.add("Indoor")
    } else {
        tags.add("Outdoor")
    }
    
    if (descLower.contains("tropical") || descLower.contains("jungle") || descLower.contains("rainforest")) {
        tags.add("Tropical")
    } else if (descLower.contains("arid") || descLower.contains("desert") || descLower.contains("succulent")) {
        tags.add("Succulent")
    } else {
        tags.add("Herbaceous")
    }
    
    if (descLower.contains("climb") || descLower.contains("vine") || descLower.contains("ivy") || descLower.contains("creeper")) {
        tags.add("Climber")
    } else {
        tags.add("Botanical")
    }

    return tags.take(3)
}

private fun sharePlantDetailsFromEntity(context: Context, plant: PlantEntity) {
    val shareBody = """
        🌱 Plant Identified: ${plant.name}
        🔬 Scientific Name: ${plant.scientificName}
        👪 Family: ${plant.family}
        
        📝 Description:
        ${plant.description}
        
        💧 Watering Guide:
        ${plant.waterRequirements}
        
        ☀️ Sunlight Requirements:
        ${plant.sunlightRequirements}
        
        🌿 Care Instructions:
        ${plant.careInstructions}
        
        Sent from Plant Identifier AI 🌸
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Plant Identified: ${plant.name}")
        putExtra(Intent.EXTRA_TEXT, shareBody)
    }
    context.startActivity(Intent.createChooser(intent, "Share Plant Profile"))
}

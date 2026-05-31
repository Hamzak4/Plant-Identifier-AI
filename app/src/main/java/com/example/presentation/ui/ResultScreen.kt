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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.remote.PlantDetectionResult
import com.example.presentation.viewmodel.PlantViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: PlantDetectionResult,
    localImagePath: String,
    plantViewModel: PlantViewModel,
    onNavigateBack: () -> Unit,
    onIdentifyAnother: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isSaved by plantViewModel.isSaved.collectAsState()

    val tags = remember(result.name, result.description) {
        getPlantCategories(result.name, result.description, result.family)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Result Snapshot", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onBackground
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
            enter = fadeIn(animationSpec = tween(500))
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
                        val file = File(localImagePath)
                        AsyncImage(
                            model = if (file.exists()) file else localImagePath,
                            contentDescription = "Identified plant photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Soft elegant shadow overlay for confidence text readability
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

                        // Confidence Match Badge using PrimaryContainer & OnPrimaryContainer
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
                                    text = "${result.confidenceScore.toInt()}% Match",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Info Sheet (Static representation with Rounded-T 32dp corner style)
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
                            // Language switcher tab bar
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
                                    contentPadding = PaddingValues(0.dp)
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
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("اردو گائیڈ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            val isUrdu = displayLanguage == "Urdu"
                            val displayName = if (isUrdu && result.nameUrdu.isNotBlank()) result.nameUrdu else result.name
                            val displayFamily = if (isUrdu && result.familyUrdu.isNotBlank()) result.familyUrdu else result.family
                            val displayDesc = if (isUrdu && result.descriptionUrdu.isNotBlank()) result.descriptionUrdu else result.description
                            val displayCare = if (isUrdu && result.careInstructionsUrdu.isNotBlank()) result.careInstructionsUrdu else result.careInstructions
                            val displayWater = if (isUrdu && result.waterRequirementsUrdu.isNotBlank()) result.waterRequirementsUrdu else result.waterRequirements
                            val displaySun = if (isUrdu && result.sunlightRequirementsUrdu.isNotBlank()) result.sunlightRequirementsUrdu else result.sunlightRequirements

                            // Title row with name and a green ecological florist icon
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
                                        text = result.scientificName,
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

                            // Quick dynamic botanical tags
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

                            // Quick Care Grid matching watering and sunlight with exact custom items
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuickCareItem(
                                    modifier = Modifier.weight(1f),
                                    emoji = "💧",
                                    label = if (isUrdu) "پانی کی ضرورت" else "Watering",
                                    value = displayWater
                                )
                                QuickCareItem(
                                    modifier = Modifier.weight(1f),
                                    emoji = "☀️",
                                    label = if (isUrdu) "سورج کی روشنی" else "Sunlight",
                                    value = displaySun
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Clean, uncluttered Editorial Description Section
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

                // Premium Material 3 Footer Action Bar with Save to Collection and Share Icon Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save to DB collection button
                    Button(
                        onClick = { plantViewModel.savePlantToHistory(result, localImagePath) },
                        enabled = !isSaved,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("save_result_btn")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSaved) "Saved to Journal" else "Save to Collection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Share Button (Beautiful rounded circular layout as represented in Design HTML)
                    Button(
                        onClick = { sharePlantDetails(context, result) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("share_result_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share details",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Extra scan another action to return gracefully back to identifying
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                ) {
                    Button(
                        onClick = onIdentifyAnother,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("scan_another_btn")
                    ) {
                        Text(text = "Identify Another Plant", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickCareItem(
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
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Global tag helper so we correctly reflect plant categories beautifully
fun getPlantCategories(plantName: String, description: String, family: String): List<String> {
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

private fun sharePlantDetails(context: Context, plant: PlantDetectionResult) {
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
    context.startActivity(Intent.createChooser(intent, "Share Plant Details"))
}

package com.example.presentation.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.UserSession
import com.example.presentation.viewmodel.AuthViewModel

// High-fidelity structures representing interactive aura botanical profiles
data class BotanicVibe(
    val id: String,
    val name: String,
    val nameUrdu: String,
    val mainColor: Color,
    val gradientColors: List<Color>,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userSession: UserSession,
    authViewModel: AuthViewModel,
    scannedPlantsCount: Int,
    savedEssaysCount: Int,
    onNavigateToReminders: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State parameters for premium edit name dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userSession.fullName) }

    // State parameters for dynamic preferences with persistent visual changes
    var preferredLanguage by remember { mutableStateOf("Bilingual") } // "Bilingual" or "English Only"
    var waterMeasurementUnit by remember { mutableStateOf("Metric") } // "Metric" or "Imperial"
    var notificationEnabled by remember { mutableStateOf(true) }

    // Predefined stunning botanical theme vibes for interactive customization
    val vibes = remember {
        listOf(
            BotanicVibe(
                id = "emerald",
                name = "Lush Emerald",
                nameUrdu = "سبز زمرد",
                mainColor = Color(0xFF2E7D32),
                gradientColors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50), Color(0xFFC8E6C9)),
                description = "Classic dense botanical woodland theme"
            ),
            BotanicVibe(
                id = "sakura",
                name = "Sakura Bloom",
                nameUrdu = "کوبل شفتالو",
                mainColor = Color(0xFFD81B60),
                gradientColors = listOf(Color(0xFF880E4F), Color(0xFFEC407A), Color(0xFFF8BBD0)),
                description = "Warm orchid floral bloom theme"
            ),
            BotanicVibe(
                id = "desert",
                name = "Desert Amber",
                nameUrdu = "ریگستانی ریت",
                mainColor = Color(0xFFE65100),
                gradientColors = listOf(Color(0xFFBF360C), Color(0xFFFF9800), Color(0xFFFFE0B2)),
                description = "Golden sand succulent theme"
            ),
            BotanicVibe(
                id = "midnight",
                name = "Midnight Oasis",
                nameUrdu = "رات کا نخلستان",
                mainColor = Color(0xFF006064),
                gradientColors = listOf(Color(0xFF004D40), Color(0xFF00ACC1), Color(0xFFB2EBF2)),
                description = "Deep translucent moonlit stream theme"
            )
        )
    }

    var selectedVibe by remember { mutableStateOf(vibes[0]) }

    // Dynamic Experience Level Formula & Level Subtitle
    val experienceScore = remember(scannedPlantsCount, savedEssaysCount) {
        (scannedPlantsCount * 15 + savedEssaysCount * 20).coerceIn(5, 100)
    }

    val (rankLevel, rankLevelUrdu) = remember(scannedPlantsCount) {
        when {
            scannedPlantsCount == 0 -> "Seedling Scout" to "نیا نوخیز پودا"
            scannedPlantsCount in 1..2 -> "Sprout Guardian" to "شاخسار محافظ"
            scannedPlantsCount in 3..5 -> "Blossom Botanist" to "گلزار ماہرِ نباتات"
            else -> "Elite Flora Doctor 🌿" to "اعلیٰ معالجِ نباتات"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Gardener Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("اپنا پروفائل اور سیٹنگز", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            tempName = userSession.fullName
                            showEditDialog = true
                        },
                        modifier = Modifier.testTag("edit_profile_dialog_trigger")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Name",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Area featuring dynamic linear gradient from selected user vibe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    selectedVibe.gradientColors[0],
                                    selectedVibe.gradientColors[1].copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            // Perfect round glowing avatar backed by current vibe style
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                selectedVibe.gradientColors[0],
                                                selectedVibe.gradientColors[1]
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    selectedVibe.gradientColors[2],
                                                    selectedVibe.gradientColors[1]
                                                )
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            // Glow indicator pen overlay
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(selectedVibe.gradientColors[0], shape = CircleShape)
                                    .clickable {
                                        tempName = userSession.fullName
                                        showEditDialog = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Modify Displays",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic high fidelity botanic rank tag
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = selectedVibe.mainColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, selectedVibe.mainColor.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = selectedVibe.mainColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$rankLevel • $rankLevelUrdu",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedVibe.mainColor
                                )
                            }
                        }
                    }
                }

                // Main layout content column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // User Identity details card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userSession.fullName.ifBlank { "Botanical Caretaker" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = userSession.email,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Credentials validation stamp
                                val isFirebase = userSession.isFirebaseMode
                                val bgTint = if (isFirebase) Color(0xFFE8F5E9) else Color(0xFFECEFF1)
                                val textTint = if (isFirebase) Color(0xFF2E7D32) else Color(0xFF455A64)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(bgTint, shape = RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFirebase) Icons.Default.VerifiedUser else Icons.Default.OfflineBolt,
                                        contentDescription = null,
                                        tint = textTint,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isFirebase) "Synced Cloud Profile" else "Local Sandbox Mode",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textTint
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    tempName = userSession.fullName
                                    showEditDialog = true
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(selectedVibe.mainColor.copy(alpha = 0.1f), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit displays inline",
                                    tint = selectedVibe.mainColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Eye-Catching LevelProgression Meter & Green-Finger Score Radar (NEW AMAZING ADDITION)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, selectedVibe.mainColor.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Green Finger Index (انڈیکس سکور)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = selectedVibe.mainColor
                                    )
                                    Text(
                                        text = "Your active botanic care score",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$experienceScore%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = selectedVibe.mainColor
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Experience bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = experienceScore / 100f)
                                        .fillMaxHeight()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    selectedVibe.gradientColors[1],
                                                    selectedVibe.mainColor
                                                )
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("New Sprout", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Flora Master", fontSize = 9.sp, color = selectedVibe.mainColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Key metrics statistics section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = selectedVibe.mainColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = scannedPlantsCount.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Plants Scanned",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = selectedVibe.mainColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = savedEssaysCount.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Essays Created",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Stunning Gardener Achievements Grid (NEW AMAZING VISUAL ENHANCEMENT)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "Your Botanical Achievements",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedVibe.mainColor,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Achievement 1: First Bloom
                                val unlocked1 = scannedPlantsCount > 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (unlocked1) selectedVibe.gradientColors[2].copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Grass,
                                            contentDescription = null,
                                            tint = if (unlocked1) selectedVibe.mainColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text("First Sprout", fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                        Text(if (unlocked1) "Earned" else "Locked", fontSize = 8.sp, color = if (unlocked1) selectedVibe.mainColor else Color.Gray)
                                    }
                                }

                                // Achievement 2: Flourishing Scribe
                                val unlocked2 = savedEssaysCount > 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (unlocked2) selectedVibe.gradientColors[2].copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = if (unlocked2) selectedVibe.mainColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text("Flora Scholar", fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                        Text(if (unlocked2) "Earned" else "Locked", fontSize = 8.sp, color = if (unlocked2) selectedVibe.mainColor else Color.Gray)
                                    }
                                }

                                // Achievement 3: Seasoned Guard
                                val unlocked3 = scannedPlantsCount >= 3
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (unlocked3) selectedVibe.gradientColors[2].copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = if (unlocked3) selectedVibe.mainColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text("Root Master", fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                        Text(if (unlocked3) "Earned" else "Locked", fontSize = 8.sp, color = if (unlocked3) selectedVibe.mainColor else Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Botanic Aura Selector (Garden Themes Customizer) (NEW CHIC VISUAL COMPONENT)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Personalize Botanic Aura (آپ کا پسندیدہ تھیم)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedVibe.mainColor
                            )
                            Text(
                                text = "Instantly adapt aesthetic highlights and gradients",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                vibes.forEach { vibe ->
                                    val isSelected = selectedVibe.id == vibe.id
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .background(
                                                if (isSelected) vibe.mainColor.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                selectedVibe = vibe
                                                Toast.makeText(context, "${vibe.name} aura activated!", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(
                                                        Brush.linearGradient(colors = vibe.gradientColors.take(2)),
                                                        shape = CircleShape
                                                    )
                                                    .align(Alignment.CenterHorizontally)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                vibe.name.split(" ").last(),
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) vibe.mainColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Dynamic Preferences configs (Language and water standard controls)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Preferences & Diagnostics",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedVibe.mainColor,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            // Language Selector Switch
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Translate,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Preferred Language", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(
                                        text = preferredLanguage,
                                        fontSize = 11.sp,
                                        color = selectedVibe.mainColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Bilingual (باہمی زبان)", "English Only").forEach { lang ->
                                        val isSelected = preferredLanguage == lang.split(" ").first() || (lang.startsWith("Bilingual") && preferredLanguage == "Bilingual")
                                        val displayLabel = if (lang.startsWith("Bilingual")) "Bilingual" else "English Only"
                                        val chipColor = if (isSelected)
                                            selectedVibe.mainColor.copy(alpha = 0.15f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(chipColor, shape = RoundedCornerShape(10.dp))
                                                .clickable {
                                                    preferredLanguage = displayLabel
                                                    Toast.makeText(context, "Language updated to $displayLabel", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = lang,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) selectedVibe.mainColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            // Dosage Standards Switch (Metric/Imperial)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Measurement System", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(
                                        text = waterMeasurementUnit,
                                        fontSize = 11.sp,
                                        color = selectedVibe.mainColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Metric (ml, cm)", "Imperial (oz, in)").forEach { unit ->
                                        val unitKey = unit.split(" ").first()
                                        val isSelected = waterMeasurementUnit == unitKey
                                        val chipColor = if (isSelected)
                                            selectedVibe.mainColor.copy(alpha = 0.15f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(chipColor, shape = RoundedCornerShape(10.dp))
                                                .clickable {
                                                    waterMeasurementUnit = unitKey
                                                    Toast.makeText(context, "Water calculations changed to $unitKey", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = unit,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) selectedVibe.mainColor else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            // Care Notifications Switch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = if (notificationEnabled) selectedVibe.mainColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Plant Care Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Keep schedules synced correctly", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = notificationEnabled,
                                    onCheckedChange = { notificationEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = selectedVibe.mainColor,
                                        checkedTrackColor = selectedVibe.mainColor.copy(alpha = 0.3f),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }

                    // Direct interactive link shortcut to alarm scheduler
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToReminders() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = selectedVibe.mainColor.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, selectedVibe.mainColor.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(selectedVibe.mainColor, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = "Alert Alarms timer",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Botanical Care Reminders",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedVibe.mainColor
                                )
                                Text(
                                    text = "Set water, fertilizer, & fertilizer alert intervals",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "",
                                tint = selectedVibe.mainColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Quick Assistance inline badge guide card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Bilingual Botanical chatbot is fully online! Enjoy expert plant remedies in beautiful Urdu and English anywhere.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Premium polished solid log out button
                    Button(
                        onClick = { authViewModel.logout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log out sessions"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out of Session",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Custom gardener display name modification sheet dialog
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = selectedVibe.mainColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Display Name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Update your full name as shown inside smart reports and bilingual chat systems.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            placeholder = { Text("Gardener name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_fullname_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                authViewModel.updateProfileName(tempName.trim())
                                showEditDialog = false
                                Toast.makeText(context, "Full name verified & updated!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedVibe.mainColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_fullname_btn")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEditDialog = false }
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

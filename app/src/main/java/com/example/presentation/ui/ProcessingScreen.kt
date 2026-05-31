package com.example.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProcessingScreen(
    errorMsg: String? = null,
    onDismissOrRetry: () -> Unit = {}
) {
    val captions = listOf(
        "Scanning botanical venation patterns...",
        "Analyzing leaf structure and serrations...",
        "Identifying taxons and family lineages...",
        "Querying Gemini AI Botanical Engine...",
        "Compiling lighting and hydration parameters...",
        "Formulating tailored horticulture care guide..."
    )

    var currentCaptionIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2200)
            currentCaptionIndex = (currentCaptionIndex + 1) % captions.size
        }
    }

    if (errorMsg != null) {
        val isLeaked = errorMsg.contains("leaked", ignoreCase = true) || errorMsg.contains("leak", ignoreCase = true)
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissOrRetry,
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = onDismissOrRetry,
                    modifier = Modifier.testTag("error_dismiss_button")
                ) {
                    Text("Go Back")
                }
            },
            title = {
                Text(
                    text = if (isLeaked) "API Key Leaked & Blocked" else "Access Denied (403/Forbidden)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    if (isLeaked) {
                        Text(
                            text = "Google has flagged and disabled your current API key because it was detected as leaked in a public space (e.g. GitHub commit history or public repository).\n\n" +
                                   "How to resolve this:\n" +
                                   "1. Go to Google AI Studio (https://aistudio.google.com) and create a brand new API Key.\n" +
                                   "2. Open the Secrets panel in AI Studio (the keys icon in the left sidebar configuration panel).\n" +
                                   "3. Replace GEMINI_API_KEY with your new key and click Save.\n" +
                                   "4. Retake the picture or upload the plant image again!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("processing_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Iconic logo ring with a soft pulsing green color
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                Icon(
                    imageVector = Icons.Default.LocalFlorist,
                    contentDescription = "Pulsing Florist Emblem",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Identifying Plant Specimen",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Premium animated caption reveal
            AnimatedContent(
                targetState = captions[currentCaptionIndex],
                transitionSpec = {
                    fadeIn() with fadeOut()
                },
                modifier = Modifier.height(48.dp)
            ) { targetText ->
                Text(
                    text = targetText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

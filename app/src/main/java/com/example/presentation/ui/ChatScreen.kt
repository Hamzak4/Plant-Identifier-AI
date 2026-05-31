package com.example.presentation.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ChatMessageEntity
import com.example.presentation.viewmodel.ChatViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel
) {
    val context = LocalContext.current
    val messages by chatViewModel.chatMessages.collectAsState()
    val isTyping by chatViewModel.isTyping.collectAsState()
    val isExpertMode by chatViewModel.isExpertMode.collectAsState()

    var chatInputText by remember { mutableStateOf("") }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }

    val listState = rememberLazyListState()

    // Speech-to-text voice recognition launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenWords = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = spokenWords?.firstOrNull() ?: ""
            if (recognizedText.isNotBlank()) {
                chatInputText = recognizedText
            }
        } else {
            Toast.makeText(context, "Voice command cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // Attachment image launcher
    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedImageUri = uri
        }
    }

    // Auto scroll to bottom when messages list size changes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Botanist Assistant", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = if (isExpertMode) "Specialized Botanist Mode Active" else "General Assistant Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpertMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Expert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isExpertMode,
                            onCheckedChange = { chatViewModel.toggleExpertMode() },
                            modifier = Modifier.scale(0.8f).testTag("bot_expert_toggle")
                        )
                        IconButton(onClick = { chatViewModel.clearHistory() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat logs")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Chat messages list
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Ask Dr. Botanist AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ask about plant disease remedies, watering, soil compositions, or anything botanical. \n\nToggle 'Expert' above for scientific clinical insights.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(messages) { index, msg ->
                            ChatMessageBubble(msg = msg)
                        }

                        if (isTyping) {
                            item {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }

            // Attached image bubble preview if selected
            if (attachedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = attachedImageUri,
                                contentDescription = "Preview attachment",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Image attached for botanical scan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { attachedImageUri = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear attachment")
                        }
                    }
                }
            }

            // Suggestion chips row (only visible when chat input is empty and not typing)
            AnimatedVisibility(
                visible = chatInputText.isEmpty() && !isTyping,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val chips = listOf(
                        "Watering? 💧" to "What is the best general watering schedule for household plants?",
                        "Yellow Leaf 🍂" to "Why are my plant leaves turning yellow and pale?",
                        "Fast Growth 🌿" to "How can I boost my plant's growth speed safely?"
                    )
                    chips.forEach { (label, promptText) ->
                        SuggestionChip(
                            onClick = {
                                chatInputText = promptText
                            },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Bottom message box panel
            Surface(
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectImageLauncher.launch("image/*") }) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Attach picture",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Voice speech request
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask Roman or English botanical questions... 🌱")
                                }
                                voiceLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Speech recognition is unsupported on this virtual device.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice input",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextField(
                        value = chatInputText,
                        onValueChange = { chatInputText = it },
                        placeholder = { Text("Ask anything botanical... / سوال پوچھیں...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input")
                            .padding(horizontal = 4.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            val textToSend = chatInputText
                            val imagePathValue = attachedImageUri?.toString()
                            if (textToSend.isNotBlank() || imagePathValue != null) {
                                chatViewModel.sendMessage(textToSend, imagePathValue)
                                chatInputText = ""
                                attachedImageUri = null
                            }
                        },
                        enabled = chatInputText.isNotBlank() || attachedImageUri != null,
                        modifier = Modifier
                            .testTag("chat_send_button")
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (chatInputText.isNotBlank() || attachedImageUri != null)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (chatInputText.isNotBlank() || attachedImageUri != null)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessageEntity) {
    val isUser = msg.sender == "user"
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // If message contains attached file
                if (msg.imageUri != null) {
                    val file = File(msg.imageUri)
                    AsyncImage(
                        model = if (file.exists()) file else msg.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Parse custom bullet, italics and bold Markdown lines nicely
                MarkdownTextContainer(text = msg.text, color = contentColor)
            }
        }
    }
}

@Composable
fun MarkdownTextContainer(text: String, color: Color) {
    Column {
        val lines = text.split("\n")
        lines.forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("* ") || trimmedLine.startsWith("- ")) {
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("•", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = buildFormattedString(trimmedLine.substring(2)),
                        fontSize = 13.sp,
                        color = color,
                        lineHeight = 18.sp
                    )
                }
            } else if (trimmedLine.startsWith("#")) {
                // Header style
                val cleanHeader = trimmedLine.replace("#", "").trim()
                Text(
                    text = cleanHeader,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Text(
                    text = buildFormattedString(trimmedLine),
                    fontSize = 13.sp,
                    color = color,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}

fun buildFormattedString(input: String): String {
    // Regex matches double-asterisk **bolding** inside lines and converts back
    return input.replace("**", "")
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Botanist compiling data...",
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Simple modifier helper
fun Modifier.scale(scale: Float): Modifier = this

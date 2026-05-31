package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.testTag
import com.example.presentation.ui.CameraScreen
import com.example.presentation.ui.HistoryDetailScreen
import com.example.presentation.ui.HistoryScreen
import com.example.presentation.ui.HomeScreen
import com.example.presentation.ui.ProcessingScreen
import com.example.presentation.ui.ResultScreen
import com.example.presentation.ui.LoginScreen
import com.example.presentation.ui.RegisterScreen
import com.example.presentation.ui.ForgotPasswordScreen
import com.example.presentation.ui.ChatScreen
import com.example.presentation.ui.DiseaseScreen
import com.example.presentation.ui.EssayScreen
import com.example.presentation.ui.ProfileScreen
import com.example.presentation.ui.ReminderScreen
import com.example.presentation.viewmodel.CameraViewModel
import com.example.presentation.viewmodel.HistoryViewModel
import com.example.presentation.viewmodel.PlantUiState
import com.example.presentation.viewmodel.PlantViewModel
import com.example.presentation.viewmodel.AuthViewModel
import com.example.presentation.viewmodel.ChatViewModel
import com.example.presentation.viewmodel.DiseaseViewModel
import com.example.presentation.viewmodel.EssayViewModel
import com.example.presentation.viewmodel.ReminderViewModel
import com.example.data.repository.UserSession
import com.example.ui.theme.MyApplicationTheme

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val HOME = "home"
    const val CHAT = "chat"
    const val DISEASE = "disease"
    const val ESSAY = "essay"
    const val PROFILE = "profile"
    const val REMINDERS = "reminders"
    const val CAMERA = "camera"
    const val PROCESSING = "processing"
    const val RESULT = "result"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppNavHost()
            }
        }
    }
}

@Composable
fun MainAppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel()
    val cameraViewModel: CameraViewModel = viewModel()
    val plantViewModel: PlantViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    val diseaseViewModel: DiseaseViewModel = viewModel()
    val essayViewModel: EssayViewModel = viewModel()
    val reminderViewModel: ReminderViewModel = viewModel()

    val userSession by authViewModel.userSession.collectAsState()

    // Observe userSession to force redirect of authentication boundaries
    LaunchedEffect(userSession) {
        if (userSession == null || !userSession!!.isLoggedIn) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != Routes.LOGIN &&
                currentRoute != Routes.REGISTER &&
                currentRoute != Routes.FORGOT
            ) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute == null ||
                currentRoute == Routes.LOGIN ||
                currentRoute == Routes.REGISTER ||
                currentRoute == Routes.FORGOT
            ) {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    // Determine if we should show Bottom Bar Navigation
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val dashboardTabs = listOf(
        Routes.HOME,
        Routes.CHAT,
        Routes.DISEASE,
        Routes.ESSAY,
        Routes.PROFILE
    )
    val showBottomBar = currentRoute in dashboardTabs

    // Temp variables for the pending scanned assets
    var pendingScanPath by remember { mutableStateOf<String?>(null) }
    var pendingScanUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.testTag("app_bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = {
                            if (currentRoute != Routes.HOME) {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.LocalFlorist, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.CHAT,
                        onClick = {
                            if (currentRoute != Routes.CHAT) {
                                navController.navigate(Routes.CHAT) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Chat, contentDescription = "Chatbot") },
                        label = { Text("Chatbot", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.DISEASE,
                        onClick = {
                            if (currentRoute != Routes.DISEASE) {
                                navController.navigate(Routes.DISEASE) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Healing, contentDescription = "Disease") },
                        label = { Text("Disease", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.ESSAY,
                        onClick = {
                            if (currentRoute != Routes.ESSAY) {
                                navController.navigate(Routes.ESSAY) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Essay") },
                        label = { Text("Essay", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.PROFILE,
                        onClick = {
                            if (currentRoute != Routes.PROFILE) {
                                navController.navigate(Routes.PROFILE) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (userSession != null && userSession!!.isLoggedIn) Routes.HOME else Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Authentication routes
            composable(Routes.LOGIN) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    },
                    onNavigateToForgot = {
                        navController.navigate(Routes.FORGOT)
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN)
                    }
                )
            }

            composable(Routes.FORGOT) {
                ForgotPasswordScreen(
                    authViewModel = authViewModel,
                    onBackToLogin = {
                        navController.navigate(Routes.LOGIN)
                    }
                )
            }

            // 1. Home Dashboard
            composable(Routes.HOME) {
                HomeScreen(
                    historyViewModel = historyViewModel,
                    onNavigateToCamera = {
                        navController.navigate(Routes.CAMERA)
                    },
                    onNavigateToHistory = {
                        navController.navigate(Routes.HISTORY)
                    },
                    onNavigateToDetail = { plant ->
                        historyViewModel.selectPlant(plant)
                        navController.navigate(Routes.HISTORY_DETAIL)
                    },
                    onLogout = {
                        authViewModel.logout()
                    }
                )
            }

            // 2. Chatbot Screen
            composable(Routes.CHAT) {
                ChatScreen(chatViewModel = chatViewModel)
            }

            // 3. Disease Screen
            composable(Routes.DISEASE) {
                DiseaseScreen(diseaseViewModel = diseaseViewModel)
            }

            // 4. Essay Screen
            composable(Routes.ESSAY) {
                EssayScreen(essayViewModel = essayViewModel)
            }

            // 5. Profile Screen
            composable(Routes.PROFILE) {
                val scansCount = historyViewModel.historyState.collectAsState().value.size
                val essaysCount = essayViewModel.savedEssays.collectAsState().value.size
                val activeSession = userSession ?: UserSession("Gardener", "user@garden.com", true, false)

                ProfileScreen(
                    userSession = activeSession,
                    authViewModel = authViewModel,
                    scannedPlantsCount = scansCount,
                    savedEssaysCount = essaysCount,
                    onNavigateToReminders = {
                        navController.navigate(Routes.REMINDERS)
                    }
                )
            }

            // Reminders Screen
            composable(Routes.REMINDERS) {
                ReminderScreen(
                    reminderViewModel = reminderViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Camera Overlay Screen
            composable(Routes.CAMERA) {
                CameraScreen(
                    cameraViewModel = cameraViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onImageCaptured = { path ->
                        pendingScanPath = path
                        pendingScanUri = null
                        navController.navigate(Routes.PROCESSING)
                    },
                    onGalleryImageSelected = { uri ->
                        pendingScanUri = uri
                        pendingScanPath = null
                        navController.navigate(Routes.PROCESSING)
                    }
                )
            }

            // Processing Page
            composable(Routes.PROCESSING) {
                val uiState by plantViewModel.uiState.collectAsState()
                var displayError by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    if (pendingScanPath != null) {
                        plantViewModel.identifyPlantFromPath(pendingScanPath!!)
                    } else if (pendingScanUri != null) {
                        plantViewModel.identifyPlantFromUri(pendingScanUri!!)
                    } else {
                        navController.popBackStack()
                    }
                }

                LaunchedEffect(uiState) {
                    when (uiState) {
                        is PlantUiState.Success -> {
                            navController.navigate(Routes.RESULT) {
                                popUpTo(Routes.HOME) { inclusive = false }
                            }
                        }
                        is PlantUiState.Error -> {
                            displayError = (uiState as PlantUiState.Error).error
                        }
                        else -> {}
                    }
                }

                ProcessingScreen(
                    errorMsg = displayError,
                    onDismissOrRetry = {
                        displayError = null
                        plantViewModel.resetState()
                        navController.popBackStack()
                    }
                )
            }

            // Analysis Result Details
            composable(Routes.RESULT) {
                val uiState by plantViewModel.uiState.collectAsState()

                if (uiState is PlantUiState.Success) {
                    val successState = uiState as PlantUiState.Success
                    ResultScreen(
                        result = successState.result,
                        localImagePath = successState.localImagePath,
                        plantViewModel = plantViewModel,
                        onNavigateBack = {
                            plantViewModel.resetState()
                            navController.popBackStack(Routes.HOME, false)
                        },
                        onIdentifyAnother = {
                            plantViewModel.resetState()
                            navController.navigate(Routes.CAMERA) {
                                popUpTo(Routes.HOME) { inclusive = false }
                            }
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack(Routes.HOME, false)
                    }
                }
            }

            // Complete Search History Logs
            composable(Routes.HISTORY) {
                HistoryScreen(
                    historyViewModel = historyViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToDetail = { plant ->
                        historyViewModel.selectPlant(plant)
                        navController.navigate(Routes.HISTORY_DETAIL)
                    }
                )
            }

            // Selected History Journal Entry Detail View
            composable(Routes.HISTORY_DETAIL) {
                val selectedPlant by historyViewModel.selectedPlant.collectAsState()

                selectedPlant?.let { plant ->
                    HistoryDetailScreen(
                        plant = plant,
                        historyViewModel = historyViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                } ?: run {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

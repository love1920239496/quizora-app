package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QuizoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: QuizoraViewModel = viewModel()
                val progress by viewModel.userProgress.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                if (progress.selectedLanguage.isEmpty()) {
                                    navController.navigate("language_select") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    composable("language_select") {
                        LanguageSelectScreen(
                            viewModel = viewModel,
                            onLanguageSelected = {
                                navController.navigate("home") {
                                    popUpTo("language_select") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        GuestHomeScreen(
                            viewModel = viewModel,
                            onNavigateToDailyChallenge = {
                                navController.navigate("study_main")
                            },
                            onNavigateToQuizFlow = {
                                navController.navigate("study_main")
                            },
                            onNavigateToChapterProgress = {
                                navController.navigate("progress")
                            },
                            onNavigateToNotesSelector = {
                                navController.navigate("study_main")
                            },
                            onSwitchLanguageRequested = {
                                navController.navigate("language_select")
                            }
                        )
                    }

                    composable("study_main") {
                        StudyMainScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToResult = {
                                navController.navigate("results") {
                                    popUpTo("study_main") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("results") {
                        ResultScreen(
                            viewModel = viewModel,
                            onBackToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable("progress") {
                        ChapterProgressScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            val files = assets.list("")
            if (files != null) {
                // Running on a background thread prevents blocking the UI thread (eliminating ANR crashes)
                Thread {
                    for (file in files) {
                        try { assets.open(file).close() } catch (e: Exception) {}
                    }
                }.start()
            }
        } catch (e: Exception) {}
    }
}

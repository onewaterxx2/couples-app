package com.example.myp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.myp.data.SessionManager
import com.example.myp.ui.screen.*
import com.example.myp.ui.theme.MypTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MypTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val context = LocalContext.current

    // 启动时从本地恢复登录态，已登录就直接进对应界面
    val savedUser = remember { SessionManager.loadUser(context) }

    var currentUser by remember { mutableStateOf(savedUser) }
    var appState by remember {
        mutableStateOf(
            when {
                savedUser == null -> AppState.Login
                ((savedUser["coupleId"] as? Number)?.toLong() ?: 0L) > 0 -> AppState.Main
                else -> AppState.JoinCouple
            }
        )
    }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Photos) }

    when (appState) {
        AppState.Login -> {
            LoginScreen { user ->
                currentUser = user
                SessionManager.saveUser(context, user)
                val coupleId = (user["coupleId"] as? Number)?.toLong() ?: 0L
                if (coupleId > 0) {
                    appState = AppState.Main
                } else {
                    appState = AppState.JoinCouple
                }
            }
        }
        AppState.JoinCouple -> {
            currentUser?.let { user ->
                val userId = (user["userId"] as? Number)?.toLong() ?: 0L
                JoinCoupleScreen(
                    userId = userId,
                    onJoinSuccess = { joinResult ->
                        val newUser = mutableMapOf<String, Any>()
                        newUser.putAll(user)
                        joinResult["coupleId"]?.let { newUser["coupleId"] = it }
                        joinResult["startDate"]?.let { newUser["startDate"] = it }
                        currentUser = newUser
                        SessionManager.saveUser(context, newUser)
                        appState = AppState.Main
                    },
                    onSkip = {
                        appState = AppState.Main
                    }
                )
            }
        }
        AppState.Main -> {
            currentUser?.let { user ->
                val coupleId = (user["coupleId"] as? Number)?.toLong() ?: 0L
                val userId = (user["userId"] as? Number)?.toLong() ?: 0L
                val isMale = user["isMale"] as? Boolean ?: true
                val nickname = user["nickname"] as? String ?: ""
                val startDate = user["startDate"] as? String

                MainScreen(
                    coupleId = coupleId,
                    userId = userId,
                    isMale = isMale,
                    nickname = nickname,
                    startDate = startDate,
                    currentScreen = currentScreen,
                    onScreenChange = { currentScreen = it },
                    onLogout = {
                        SessionManager.clear(context)
                        currentUser = null
                        currentScreen = Screen.Photos
                        appState = AppState.Login
                    }
                )
            }
        }
    }
}

sealed class AppState {
    object Login : AppState()
    object JoinCouple : AppState()
    object Main : AppState()
}
package com.example.myp.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myp.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (Map<String, Any>) -> Unit) {
    val authViewModel: AuthViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }
    var code by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }

    val loginResult by authViewModel.loginResult.observeAsState()
    val registerResult by authViewModel.registerResult.observeAsState()
    val sendCodeResult by authViewModel.sendCodeResult.observeAsState()

    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            isLoading = false
            val success = result["success"] as? Boolean ?: false
            if (success) {
                onLoginSuccess(result)
            } else {
                feedbackMessage = result["message"] as? String ?: "登录失败"
            }
        }
    }

    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            isLoading = false
            val success = result["success"] as? Boolean ?: false
            if (success) {
                isRegister = false
                code = ""
                feedbackMessage = "注册成功，请登录"
            } else {
                feedbackMessage = result["message"] as? String ?: "注册失败"
            }
        }
    }

    LaunchedEffect(sendCodeResult) {
        sendCodeResult?.let { result ->
            val success = result["success"] as? Boolean ?: false
            if (success) {
                feedbackMessage = ""
                countdown = 60
            } else {
                feedbackMessage = result["message"] as? String ?: "验证码发送失败"
                countdown = 0
            }
        }
    }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(34.dp))

            BrandLockup(isRegister = isRegister)

            Spacer(modifier = Modifier.height(26.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                tonalElevation = 2.dp,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isRegister) "创建账号" else "账号登录",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FeedbackMessage(message = feedbackMessage)

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim() },
                        label = { Text("邮箱") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isRegister) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = code,
                                onValueChange = { code = it },
                                label = { Text("验证码") },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(
                                onClick = {
                                    feedbackMessage = ""
                                    if (email.isBlank()) {
                                        feedbackMessage = "请先填写邮箱"
                                    } else {
                                        authViewModel.sendCode(email)
                                    }
                                },
                                enabled = countdown == 0,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .widthIn(min = 104.dp)
                                    .height(56.dp)
                            ) {
                                Text(if (countdown > 0) "${countdown}s" else "发送")
                            }
                        }

                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("昵称") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GenderOption(
                                label = "他",
                                selected = isMale,
                                onClick = { isMale = true },
                                modifier = Modifier.weight(1f)
                            )
                            GenderOption(
                                label = "她",
                                selected = !isMale,
                                onClick = { isMale = false },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("密码") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            feedbackMessage = ""
                            isLoading = false
                            if (isRegister) {
                                if (email.isBlank() || password.isBlank() || nickname.isBlank() || code.isBlank()) {
                                    feedbackMessage = "请填写完整信息"
                                } else {
                                    isLoading = true
                                    authViewModel.register(email, password, nickname, isMale, code)
                                }
                            } else {
                                if (email.isBlank() || password.isBlank()) {
                                    feedbackMessage = "请填写邮箱和密码"
                                } else {
                                    isLoading = true
                                    authViewModel.login(email, password)
                                }
                            }
                        },
                        enabled = !isLoading,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isRegister) "完成注册" else "进入空间",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = {
                    isRegister = !isRegister
                    feedbackMessage = ""
                    code = ""
                },
                modifier = Modifier.padding(top = 14.dp)
            ) {
                Text(
                    text = if (isRegister) "已有账号，去登录" else "没有账号，创建一个",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BrandLockup(isRegister: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.58f))
            )
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "四叶草",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isRegister) "从一个账号开始，建立你们的专属空间" else "回到你们共同保存的日常",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun FeedbackMessage(message: String) {
    if (message.isBlank()) return

    val isSuccess = message.contains("成功")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun GenderOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

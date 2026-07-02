package com.example.myp.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myp.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinCoupleScreen(
    userId: Long,
    onJoinSuccess: (Map<String, Any>) -> Unit,
    onSkip: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    var code by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var myCode by remember { mutableStateOf<String?>(null) }
    var myCoupleId by remember { mutableStateOf(0L) }

    val joinCoupleResult by authViewModel.joinCoupleResult.observeAsState()
    val createCoupleResult by authViewModel.createCoupleResult.observeAsState()
    val coupleStatusResult by authViewModel.coupleStatusResult.observeAsState()

    LaunchedEffect(joinCoupleResult) {
        joinCoupleResult?.let { result ->
            isLoading = false
            val success = result["success"] as? Boolean ?: false
            if (success) {
                onJoinSuccess(result)
            } else {
                feedbackMessage = result["message"] as? String ?: "加入失败"
            }
        }
    }

    LaunchedEffect(createCoupleResult) {
        createCoupleResult?.let { result ->
            isLoading = false
            val success = result["success"] as? Boolean ?: false
            if (success) {
                myCode = result["code"] as? String
                myCoupleId = (result["coupleId"] as? Number)?.toLong() ?: 0L
                feedbackMessage = ""
            } else {
                feedbackMessage = result["message"] as? String ?: "创建失败"
            }
        }
    }

    LaunchedEffect(myCoupleId) {
        if (myCoupleId > 0) {
            while (true) {
                delay(3000)
                authViewModel.checkCoupleStatus(myCoupleId)
            }
        }
    }

    LaunchedEffect(coupleStatusResult) {
        coupleStatusResult?.let { result ->
            val paired = result["paired"] as? Boolean ?: false
            if (paired) {
                val data = mutableMapOf<String, Any>()
                data["success"] = true
                result["coupleId"]?.let { data["coupleId"] = it }
                result["startDate"]?.let { data["startDate"] = it }
                onJoinSuccess(data)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.52f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 12.dp,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "连接你们的空间",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "一方创建情侣码，另一方输入同一个码即可绑定。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .widthIn(max = 300.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (feedbackMessage.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = feedbackMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            CreateCodePanel(
                myCode = myCode,
                myCoupleId = myCoupleId,
                isLoading = isLoading,
                createStartDate = createCoupleResult?.get("startDate"),
                onCreate = {
                    feedbackMessage = ""
                    isLoading = true
                    authViewModel.createCouple(userId)
                },
                onEnterNow = { data ->
                    onJoinSuccess(data)
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "或",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            JoinCodePanel(
                code = code,
                isLoading = isLoading,
                onCodeChange = { code = it.uppercase() },
                onJoin = {
                    feedbackMessage = ""
                    if (code.isBlank()) {
                        feedbackMessage = "请输入情侣码"
                    } else {
                        isLoading = true
                        authViewModel.joinCouple(userId, code)
                    }
                }
            )

            TextButton(
                onClick = onSkip,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("暂不绑定", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CreateCodePanel(
    myCode: String?,
    myCoupleId: Long,
    isLoading: Boolean,
    createStartDate: Any?,
    onCreate: () -> Unit,
    onEnterNow: (Map<String, Any>) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        tonalElevation = 2.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "创建情侣码",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (myCode != null) {
                Text(
                    "把这串码发给对方",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = myCode,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 14.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "正在等待对方加入",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                TextButton(
                    onClick = {
                        val data = mutableMapOf<String, Any>()
                        data["success"] = true
                        data["coupleId"] = myCoupleId
                        createStartDate?.let { data["startDate"] = it }
                        onEnterNow(data)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("先进入应用")
                }
            } else {
                Text(
                    "适合你先发起绑定。创建后，对方输入你的情侣码即可加入。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onCreate,
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .height(52.dp)
                ) {
                    Text("生成情侣码", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinCodePanel(
    code: String,
    isLoading: Boolean,
    onCodeChange: (String) -> Unit,
    onJoin: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.GroupAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "输入对方的码",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            OutlinedTextField(
                value = code,
                onValueChange = onCodeChange,
                label = { Text("情侣码") },
                placeholder = { Text("如 ABC123") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedButton(
                onClick = onJoin,
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("加入空间", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

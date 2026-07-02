package com.example.myp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myp.data.model.Mood
import com.example.myp.ui.theme.FemaleTint
import com.example.myp.ui.theme.MaleTint
import com.example.myp.viewmodel.MoodViewModel

private val MOOD_ICONS = listOf("😊", "🥰", "😎", "😢", "😤")
private val MOOD_LABELS = listOf("开心", "甜蜜", "帅气", "难过", "生气")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(coupleId: Long, userId: Long, isMale: Boolean) {
    val moodViewModel: MoodViewModel = viewModel()
    val todayMoods by moodViewModel.todayMoods.observeAsState(emptyList())
    var showSetDialog by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf(0) }
    var moodMessage by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    val allMoods by moodViewModel.allMoods.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        moodViewModel.loadTodayMoods(coupleId)
    }

    if (showHistory) {
        MoodHistoryScreen(allMoods, userId, isMale) { showHistory = false }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "今天你们的心情",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // 对方一定是当前用户的异性，据此把每条心情归到男方/女方
                val myMood = todayMoods.find { it.userId == userId }
                val partnerMood = todayMoods.find { it.userId != userId }
                val maleMood = if (isMale) myMood else partnerMood
                val femaleMood = if (isMale) partnerMood else myMood

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MoodDisplayCard(
                        label = "他",
                        mood = maleMood,
                        isMale = true,
                        modifier = Modifier.weight(1f)
                    )
                    MoodDisplayCard(
                        label = "她",
                        mood = femaleMood,
                        isMale = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 我的心情
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showSetDialog = true },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (myMood != null) {
                            val moodIndex = myMood.moodType.coerceIn(0, MOOD_ICONS.size - 1)
                            Text(
                                "我的心情",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(MOOD_ICONS[moodIndex], fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                MOOD_LABELS[moodIndex],
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (!myMood.message.isNullOrEmpty()) {
                                Text(
                                    myMood.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                            Text(
                                "点击修改",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        } else {
                            Text("😊", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "记录今天的心情",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "点击设置，让对方看到你的状态",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        moodViewModel.loadAllMoods(coupleId)
                        showHistory = true
                    }
                ) {
                    Text("查看历史心情")
                }
            }

            FloatingActionButton(
                onClick = { showSetDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "设置心情")
            }
        }
    }

    if (showSetDialog) {
        AlertDialog(
            onDismissRequest = { showSetDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text("设置今日心情", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(
                        "选择心情",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MOOD_ICONS.forEachIndexed { index, icon ->
                            val selected = selectedMood == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .selectable(
                                        selected = selected,
                                        onClick = { selectedMood = index }
                                    )
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(icon, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    MOOD_LABELS[index],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = moodMessage,
                        onValueChange = { moodMessage = it },
                        label = { Text("心情寄语（可选）") },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        moodViewModel.setMood(userId, coupleId, selectedMood, moodMessage)
                        showSetDialog = false
                        moodMessage = ""
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSetDialog = false
                    moodMessage = ""
                }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun MoodDisplayCard(label: String, mood: Mood?, isMale: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isMale) MaleTint else FemaleTint
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (mood != null) {
                val moodIndex = mood.moodType.coerceIn(0, MOOD_ICONS.size - 1)
                Text(MOOD_ICONS[moodIndex], fontSize = 44.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    MOOD_LABELS[moodIndex],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!mood.message.isNullOrEmpty()) {
                    Text(
                        mood.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                Text("·", fontSize = 44.sp, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "暂未设置",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryScreen(moods: List<Mood>, userId: Long, isMale: Boolean, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 内联返回头（顶部应用栏由外层 MainScreen 提供，这里不再叠一层）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                "心情历史",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (moods.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "暂无历史心情",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(moods, key = { it.id }) { mood ->
                    // 该条心情是不是我发的：是我则用我的性别，否则用对方性别（异性）
                    val moodIsMale = if (mood.userId == userId) isMale else !isMale
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = if (moodIsMale) MaleTint else FemaleTint
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    if (moodIsMale) "他" else "她",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    mood.moodDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val moodIndex = mood.moodType.coerceIn(0, MOOD_ICONS.size - 1)
                                Text(MOOD_ICONS[moodIndex], fontSize = 28.sp)
                                Column(modifier = Modifier.padding(start = 10.dp)) {
                                    Text(
                                        MOOD_LABELS[moodIndex],
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!mood.message.isNullOrEmpty()) {
                                        Text(
                                            mood.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

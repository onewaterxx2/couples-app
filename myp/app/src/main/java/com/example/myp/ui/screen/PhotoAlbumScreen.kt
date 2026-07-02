package com.example.myp.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myp.data.api.RetrofitClient
import com.example.myp.data.model.Photo
import com.example.myp.ui.theme.AccentRose
import com.example.myp.viewmodel.PhotoViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAlbumScreen(coupleId: Long, userId: Long, isMale: Boolean) {
    val photoViewModel: PhotoViewModel = viewModel()
    val photos by photoViewModel.photos.observeAsState(emptyList())
    val isLoading by photoViewModel.isLoading.observeAsState(false)
    val deleteResult by photoViewModel.deleteResult.observeAsState()
    val context = LocalContext.current
    var showUploadDialog by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var fullScreenIndex by remember { mutableStateOf(-1) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<Photo?>(null) }

    // 监听删除结果
    LaunchedEffect(deleteResult) {
        deleteResult?.let { (success, message) ->
            // 显示提示消息
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        showUploadDialog = uri != null
    }

    LaunchedEffect(Unit) {
        photoViewModel.loadPhotos(coupleId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (photos.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "还没有照片",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "点击右下角，上传你们的第一张合影",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoCard(
                        photo = photo,
                        userId = userId,
                        isMale = isMale,
                        onLike = { photoId, currentLikes ->
                            photoViewModel.likePhoto(photoId, currentLikes)
                        },
                        onClick = { fullScreenIndex = photos.indexOf(photo) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { imagePickerLauncher.launch("image/*") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "上传照片")
        }
    }

    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text("上传照片", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(
                        "已选择图片，可以加一句描述",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("照片描述（可选）") },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedImageUri?.let { uri ->
                            uploadPhoto(uri, coupleId, userId, description, context) {
                                showUploadDialog = false
                                description = ""
                                selectedImageUri = null
                                photoViewModel.loadPhotos(coupleId)
                            }
                        }
                    }
                ) {
                    Text("上传")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUploadDialog = false
                    description = ""
                    selectedImageUri = null
                }) {
                    Text("取消")
                }
            }
        )
    }

    // 全屏查看：左右滑动翻页 + 双指缩放 + 拖动 + 双击放大
    if (fullScreenIndex >= 0 && photos.isNotEmpty()) {
        FullScreenPhotoViewer(
            photos = photos,
            startIndex = fullScreenIndex.coerceIn(0, photos.size - 1),
            userId = userId,
            coupleId = coupleId,
            photoViewModel = photoViewModel,
            onDismiss = { fullScreenIndex = -1 }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPhotoViewer(
    photos: List<Photo>,
    startIndex: Int,
    userId: Long,
    coupleId: Long,
    photoViewModel: PhotoViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val pagerState = rememberPagerState(initialPage = startIndex) { photos.size }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val photo = photos[page]
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                // 翻页后把上一页的缩放/位移重置
                LaunchedEffect(pagerState.currentPage) {
                    if (pagerState.currentPage != page) {
                        scale = 1f
                        offset = Offset.Zero
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onDismiss() },
                                onDoubleTap = {
                                    if (scale > 1f) {
                                        scale = 1f
                                        offset = Offset.Zero
                                    } else {
                                        scale = 2.5f
                                    }
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset = if (scale > 1f) offset + pan else Offset.Zero
                            }
                        }
                ) {
                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = photo.description,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                    if (!photo.description.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(16.dp)
                        ) {
                            Text(photo.description, color = Color.White)
                        }
                    }
                }
            }

            // 页码指示
            Text(
                text = "${pagerState.currentPage + 1} / ${photos.size}",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "关闭", tint = Color.White)
            }

            // 删除按钮（只有照片上传者才能看到）
            val currentPhoto = photos[pagerState.currentPage]
            if (currentPhoto.userId == userId) {
                IconButton(
                    onClick = {
                        photoViewModel.deletePhoto(currentPhoto.id, userId, coupleId)
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoCard(
    photo: Photo,
    userId: Long,
    isMale: Boolean,
    onLike: (Long, Int) -> Unit,
    onClick: () -> Unit
) {
    val isMe = photo.userId == userId
    val dateStr = try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(photo.createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        dateTime.format(formatter)
    } catch (e: Exception) {
        if (photo.createdAt.length >= 16) photo.createdAt.substring(0, 16) else photo.createdAt
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onClick() }
            ) {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (!photo.description.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            photo.description,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (isMe) "我" else if (isMale) "她" else "他",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onLike(photo.id, photo.likes) }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = if (photo.likes > 0) AccentRose else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        photo.likes.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun uploadPhoto(
    uri: Uri,
    coupleId: Long,
    userId: Long,
    description: String,
    context: android.content.Context,
    onSuccess: () -> Unit
) {
    Thread {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_image.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestBody
            )

            val response = RetrofitClient.apiService.uploadPhoto(
                coupleId,
                userId,
                description,
                filePart
            ).execute()

            if (response.isSuccessful) {
                onSuccess()
            }
        } catch (e: Exception) {
            Log.e("PhotoUpload", "Upload failed", e)
        }
    }.start()
}
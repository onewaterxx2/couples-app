package com.example.myp.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Photo(
    val id: Long,
    @SerializedName("coupleId")
    val coupleId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("imageUrl")
    val imageUrl: String,
    val description: String?,
    val likes: Int,
    @SerializedName("createdAt")
    val createdAt: String
)
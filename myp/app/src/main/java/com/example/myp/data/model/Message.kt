package com.example.myp.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    val id: Long,
    @SerializedName("coupleId")
    val coupleId: Long,
    @SerializedName("senderId")
    val senderId: Long,
    val content: String,
    @SerializedName("createdAt")
    val createdAt: String
)
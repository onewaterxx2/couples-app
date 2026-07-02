package com.example.myp.data.model

import com.google.gson.annotations.SerializedName

data class Mood(
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("coupleId")
    val coupleId: Long,
    @SerializedName("moodType")
    val moodType: Int,
    val message: String?,
    @SerializedName("moodDate")
    val moodDate: String
)
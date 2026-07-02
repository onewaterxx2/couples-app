package com.example.myp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long,
    val phone: String,
    val nickname: String,
    @SerializedName("isMale")
    val isMale: Boolean,
    @SerializedName("coupleId")
    val coupleId: Long?
)
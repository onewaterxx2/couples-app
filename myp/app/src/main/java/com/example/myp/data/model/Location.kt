package com.example.myp.data.model

import com.google.gson.annotations.SerializedName

data class Location(
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class LocationStatus(
    val sharingEnabled: Boolean,
    val hasLocation: Boolean,
    val updatedAt: String?
)

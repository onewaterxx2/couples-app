package com.example.myp.data.model

data class CoupleTask(
    val id: Long,
    val coupleId: Long,
    val creatorId: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String
)

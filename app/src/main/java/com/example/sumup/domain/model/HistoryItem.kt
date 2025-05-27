package com.example.sumup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryItem(
    val id: String,
    val title: String,
    val preview: String,
    val timestamp: String,
    val wordReduction: String,
    val createdAt: Long
) : Parcelable

data class HistorySection(
    val title: String,
    val items: List<HistoryItem>
)
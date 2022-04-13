package com.example.heartratemonitoringapp.data.source.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "band")
data class Entities(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: Int?,
)

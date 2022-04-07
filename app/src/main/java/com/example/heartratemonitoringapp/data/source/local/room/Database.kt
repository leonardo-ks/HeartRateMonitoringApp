package com.example.heartratemonitoringapp.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.heartratemonitoringapp.data.source.local.entities.Entities

@Database(
    entities = [Entities::class],
    exportSchema = false,
    version = 1
)
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao
}
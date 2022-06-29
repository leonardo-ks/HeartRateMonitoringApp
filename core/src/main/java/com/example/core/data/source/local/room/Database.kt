package com.example.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.data.source.local.entities.MonitoringDataEntities

@Database(
    entities = [MonitoringDataEntities::class],
    exportSchema = false,
    version = 2
)
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao
}
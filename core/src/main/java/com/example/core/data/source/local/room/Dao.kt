package com.example.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.core.data.source.local.entities.MonitoringDataEntities

@Dao
interface Dao {
    @Query("SELECT * FROM monitoring_data")
    fun getMonitoringDataList(): List<MonitoringDataEntities>

    @Query("DELETE FROM monitoring_data WHERE id = :id")
    fun deleteMonitoringDataById(id: Int)

    @Query("DELETE FROM monitoring_data WHERE created_at = :createdAt")
    fun deleteMonitoringDataByDate(createdAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMonitoringData(monitoringData: MonitoringDataEntities)
}
package com.example.heartratemonitoringapp.app.monitoring.ble

import java.util.*

object UUIDs {
    val BASIC_SERVICE: UUID = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb")
    val BASIC_STEP_CHARACTERISTIC: UUID = UUID.fromString("00000007-0000-3512-2118-0009af100700")

    val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    val HEART_RATE_MEASUREMENT_CHARACTERISTIC: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    val HEART_RATE_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    val HEART_RATE_CONTROL_CHARACTERISTIC: UUID = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb")
}
package com.example.heartratemonitoringapp.app.monitoring.background

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.*
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.MainActivity
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE
import com.example.heartratemonitoringapp.app.monitoring.ble.BLEService
import com.example.heartratemonitoringapp.app.monitoring.ble.BLEService.LocalBinder
import com.example.heartratemonitoringapp.app.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.app.monitoring.live.LiveMonitoringActivity
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule


class BackgroundMonitoringService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var countDownTimer: CountDownTimer
    private val timer = Timer()
    private val heartList = arrayListOf<Int>()
    private val stepList = arrayListOf<Int>()
    private val channelId = "Background Monitoring"
    private val useCase: IUseCase by inject()
    private var bluetoothService : BLEService? = null
    private var period: Long = 60000
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(
                channelId,
                "Background Monitoring Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManager.createNotificationChannel(channel)

        val bindIntent = Intent(this, BLEService::class.java)
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            val binder = service as LocalBinder
            bluetoothService = binder.getService()
            bluetoothService?.let { bluetooth ->
                if (!bluetooth.initialize()) {
                    Log.e(LiveMonitoringActivity.TAG, "Unable to initialize Bluetooth")
                } else {
                    bluetooth.connect(BLE.bluetoothDevice?.address.toString())
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        if (bluetoothService != null) {
            val result = BLE.bluetoothDevice?.let { bluetoothService!!.connect(it.address) }
            Log.d(LiveMonitoringActivity.TAG, "Connect request result=$result")
        }

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        BLE.bluetoothAdapter = bluetoothManager.adapter
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)

        if (connectedDevices.isNotEmpty()) {
            if (BLE.bluetoothDevice == null) {
                BLE.bluetoothDevice = connectedDevices.first()
            }
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(resources.getString(R.string.background_monitoring))
            .setSmallIcon(R.drawable.ic_watch)
            .setContentIntent(pendingIntent)

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                notification.setContentText(getText())
                notificationManager.notify(1, notification.build())
                getStep()
            }

            override fun onFinish() {
                val avgHeart = heartList.average().toInt()
                val stepChanges = stepList.last() - stepList.first()
                val step = stepList.maxOrNull()
                Log.d("average", "HR: $avgHeart, Step: $stepChanges")
                sendData(avgHeart, stepChanges, step?: 0)
                notificationManager.cancel(1)
            }
        }
        return START_STICKY
    }

    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BLEService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    scope.launch {
                        heartList.clear()
                        stepList.clear()
                        countDownTimer.start()
                    }
                }
                BLEService.ACTION_HR_AVAILABLE -> {
                    if (intent.extras != null) {
                        val heartRate = intent.extras?.getInt("HR")
                        if (heartRate != null) {
                            heartList.add(heartRate)
                        }
                    }
                }
                BLEService.ACTION_STEP_AVAILABLE -> {
                    if (intent.extras != null) {
                        scanHeartRate()
                        val step = intent.extras?.getInt("step")
                        if (step != null) {
                            stepList.add(step)
                        }
                    }
                }
            }
        }
    }

    private fun getText(): String {
        return if (heartList.size > 0 && stepList.size > 0) {
            "Heartrate: ${heartList.last()}, Step: ${stepList.last()}"
        } else {
            "Menghubungkan"
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED)
            addAction(BLEService.ACTION_HR_AVAILABLE)
            addAction(BLEService.ACTION_STEP_AVAILABLE)
        }
    }

    override fun onDestroy() {
        clear()
        unregisterReceiver(gattUpdateReceiver)
        job.cancel()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    fun clear() {
        countDownTimer.cancel()
        timer.cancel()
        timer.purge()
        heartList.clear()
        stepList.clear()
        BLE.bluetoothGatt?.disconnect()
        notificationManager.deleteNotificationChannel(channelId)
    }

    @SuppressLint("MissingPermission")
    private fun scanHeartRate() {
        if (BLE.bluetoothGatt != null) {
            val bluetoothCharacteristic = BLE.bluetoothGatt?.getService(UUIDs.HEART_RATE_SERVICE)?.getCharacteristic(UUIDs.HEART_RATE_CONTROL_CHARACTERISTIC)
            bluetoothCharacteristic?.value = byteArrayOf(21, 1, 1)
            if (bluetoothCharacteristic != null) {
                bluetoothService?.writeCharacteristic(bluetoothCharacteristic)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getStep() {
        val basicService = BLE.bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
        val stepLevel = basicService?.getCharacteristic(UUIDs.BASIC_STEP_CHARACTERISTIC)
        if (stepLevel != null) {
            bluetoothService?.readCharacteristic(stepLevel)
        }
    }

    private fun sendData(avgHeart: Int, stepChanges: Int, step: Int) {
        scope.launch {
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.now().format(format)
            useCase.insertMonitoringData(
                MonitoringDataDomain(
                    avgHeartRate = avgHeart,
                    stepChanges = stepChanges,
                    createdAt = date.toString(),
                    label = "",
                    userId = useCase.getUserId().first(),
                    step = step
                )
            )
            period = useCase.getMonitoringPeriod().first().toLong()
            useCase.addData(useCase.getBearer().first().toString(), avgHeart, stepChanges, step,"").collect {
                when (it) {
                    is Resource.Success -> {
                        stepList.clear()
                        heartList.clear()
                        useCase.deleteMonitoringDataByDate(date)
                        timer.schedule(period - 60000) {
                            countDownTimer.start()
                        }
                    }
                    else -> {
                        stepList.clear()
                        heartList.clear()
                        timer.schedule(period - 60000) {
                            countDownTimer.start()
                        }
                        Log.d("failed", "failed sending data")
                    }
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
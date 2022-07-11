package com.example.heartratemonitoringapp.monitoring.background

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
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.form.FormActivity
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.monitoring.ble.BLEService
import com.example.heartratemonitoringapp.monitoring.ble.BLEService.LocalBinder
import com.example.heartratemonitoringapp.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.monitoring.live.LiveMonitoringActivity
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
    private var minLimit = 0
    private var maxLimit = 0
    private var isAnomalyDetected = false
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
        scope.launch {
            minLimit = useCase.getMinHRLimit().first()
            maxLimit = useCase.getMaxHRLimit().first()
        }

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true

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

        val notificationIntent = Intent(this, FormActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(resources.getString(R.string.background_monitoring))
            .setSmallIcon(R.drawable.ic_watch)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setContentText(getString(R.string.anomaly_detected))

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                if (isAnomalyDetected) {
                    notificationManager.notify(1, notification.build())
                    cancel()
                    onFinish()
                    if (stepList.isNotEmpty() && heartList.isNotEmpty()) {
                        val avgHeart = heartList.average().toInt()
                        val stepChanges = stepList.last() - stepList.first()
                        val step = stepList.maxOrNull()
                        notificationIntent.putExtra("avgHeart", avgHeart)
                        notificationIntent.putExtra("stepChanges", stepChanges)
                        notificationIntent.putExtra("step", step)
                    }
                    useCase.setBackgroundMonitoringState(false)
                }
                getStep()
            }

            override fun onFinish() {
                if (heartList.isNotEmpty() && stepList.isNotEmpty()) {
                    val avgHeart = heartList.average().toInt()
                    val stepChanges = stepList.last() - stepList.first()
                    val step = stepList.maxOrNull()
                    Log.d("average", "HR: $avgHeart, Step: $stepChanges")
                    sendData(avgHeart, stepChanges, step ?: 0)
                }
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
                            if (heartRate < minLimit || heartRate > maxLimit) {
                                isAnomalyDetected = true
                            }
                        }
                    }
                }
                BLEService.ACTION_STEP_AVAILABLE -> {
                    scanHeartRate()
                    if (intent.extras != null) {
                        val step = intent.extras?.getInt("step")
                        if (step != null) {
                            stepList.add(step)
                        }
                    }
                }
            }
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
        unbindService(serviceConnection)
        unregisterReceiver(gattUpdateReceiver)
        job.cancel()
        isRunning = false
        super.onDestroy()
    }

    private fun clear() {
        countDownTimer.cancel()
        timer.cancel()
        timer.purge()
        heartList.clear()
        stepList.clear()
        BLE.bluetoothGatt?.disconnect()
        notificationManager.deleteNotificationChannel(channelId)
    }

    private fun scanHeartRate() {
        if (BLE.bluetoothGatt != null) {
            val bluetoothCharacteristic = BLE.bluetoothGatt?.getService(UUIDs.HEART_RATE_SERVICE)?.getCharacteristic(
                UUIDs.HEART_RATE_CONTROL_CHARACTERISTIC)
            bluetoothCharacteristic?.value = byteArrayOf(21, 1, 1)
            if (bluetoothCharacteristic != null) {
                bluetoothService?.writeCharacteristic(bluetoothCharacteristic)
            }
        }
    }

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
            val labels = useCase.findData(useCase.getBearer().first().toString(), avgHeart, stepChanges).first().data
            if (labels != null) {
                for (label in labels) {
                    useCase.insertMonitoringData(
                        MonitoringDataDomain(
                            avgHeartRate = avgHeart,
                            stepChanges = stepChanges,
                            createdAt = date.toString(),
                            label = label,
                            userId = useCase.getUserId().first(),
                            step = step
                        )
                    )
                    period = useCase.getMonitoringPeriod().first().toLong()
                    useCase.addData(useCase.getBearer().first().toString(), avgHeart, stepChanges, step, label, "").collect {
                        when (it) {
                            is Resource.Success -> {
                                stepList.clear()
                                heartList.clear()
                                useCase.deleteMonitoringDataByDate(date)
                                if (label == labels.last()) {
                                    timer.schedule(period - 60000) {
                                        countDownTimer.start()
                                    }
                                }
                            }
                            is Resource.Error -> {
                                stepList.clear()
                                heartList.clear()
                                if (label == labels.last()) {
                                    timer.schedule(period - 60000) {
                                        countDownTimer.start()
                                    }
                                }
                                Log.d("failed", "failed sending data")
                            }
                            else -> {}
                        }
                    }
                }
            } else {
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
                useCase.addData(useCase.getBearer().first().toString(), avgHeart, stepChanges, step, "", "").collect {
                    when (it) {
                        is Resource.Success -> {
                            stepList.clear()
                            heartList.clear()
                            useCase.deleteMonitoringDataByDate(date)
                            timer.schedule(period - 60000) {
                                countDownTimer.start()
                            }
                        }
                        is Resource.Error -> {
                            stepList.clear()
                            heartList.clear()
                            timer.schedule(period - 60000) {
                                countDownTimer.start()
                            }
                            Log.d("failed", "failed sending data")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    companion object {
        var isRunning = false
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
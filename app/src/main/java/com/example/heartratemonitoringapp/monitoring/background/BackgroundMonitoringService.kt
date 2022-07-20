package com.example.heartratemonitoringapp.monitoring.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.*
import android.os.CountDownTimer
import android.os.IBinder
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.dashboard.MainActivity
import com.example.heartratemonitoringapp.form.FormActivity
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.monitoring.ble.BLEService
import com.example.heartratemonitoringapp.monitoring.ble.BLEService.LocalBinder
import com.example.heartratemonitoringapp.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.monitoring.live.LiveMonitoringActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
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
    private var period: Long = 0
    private var minLimit = 0
    private var maxStillLimit = 0
    private var maxWalkLimit = 0
    private var maxLimitByAge = 0
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
            useCase.getLimit(useCase.getBearer().first().toString()).collect {
                when (it) {
                    is Resource.Success -> {
                        minLimit = it.data?.lower!!
                        maxWalkLimit = it.data?.upperWalk!!
                        maxStillLimit = it.data?.upperStill!!
                    }
                    else -> {}
                }
            }
            useCase.getProfile(useCase.getBearer().first().toString()).collect {
                when (it) {
                    is Resource.Success -> {
                        val dob = it.data?.dob.toString()
                        val format = DateTimeFormatter.ofPattern("d-MM-yyyy")
                        val formatted = LocalDate.parse(dob).format(format)
                        val now = LocalDate.now()
                        maxLimitByAge = ((220 - Period.between(LocalDate.parse(formatted, format), now).years) * 0.85).toInt()
                    }
                    else -> {}
                }
            }
            period = useCase.getMonitoringPeriod().first().toLong()
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

        val notificationIntent = Intent(this@BackgroundMonitoringService, FormActivity::class.java)
        val monitoringNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_watch)
            .setOnlyAlertOnce(true)

        val anomalyNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_watch)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentTitle(resources.getString(R.string.background_monitoring))
            .setContentText(getString(R.string.anomaly_detected))

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                getStep()
                monitoringNotification.setContentTitle(getString(R.string.background_monitoring_undergoing))
                if (heartList.size > 0 && stepList.size > 0) {
                    monitoringNotification.setContentText(getString(R.string.background_monitoring_content, heartList.last(), stepList.last()))
                } else {
                    monitoringNotification.setContentText(getString(R.string.connecting))
                }
                notificationManager.notify(1, monitoringNotification.build())
            }

            override fun onFinish() {
                if (heartList.isNotEmpty() && stepList.isNotEmpty()) {
                    notificationManager.cancel(1)
                    val avgHeart = heartList.average().toInt()
                    val stepChanges = stepList.last() - stepList.first()
                    val step = stepList.max()
                    val status = getStatus(avgHeart, stepChanges)
                    if (status != 0) {
                        notificationIntent.putExtra("avgHeartRate", avgHeart)
                        notificationIntent.putExtra("stepChanges", stepChanges)
                        notificationIntent.putExtra("step", step)
                        notificationIntent.putExtra("status", status)
                        val pendingIntent = PendingIntent.getActivity(this@BackgroundMonitoringService, 0, notificationIntent, FLAG_UPDATE_CURRENT)
                        anomalyNotification.setContentIntent(pendingIntent)
                        notificationManager.notify(2, anomalyNotification.build())
                        scope.launch {
                            useCase.sendNotification(useCase.getBearer().first().toString(), status, true)
                        }
                    } else {
                        if (heartList.isNotEmpty()) {
                            when (status) {
                                0 -> sendData(avgHeart, stepChanges, step, "Normal")
                                1 -> sendData(avgHeart, stepChanges, step, "Tidak normal 1")
                                2 -> sendData(avgHeart, stepChanges, step, "Tidak normal 2")
                                3 -> sendData(avgHeart, stepChanges, step, "Tidak normal 3")
                                4 -> sendData(avgHeart, stepChanges, step, "Tidak normal 4")
                            }
                        }
                    }
                    timer.schedule(period - 60000) {
                        countDownTimer.start()
                    }
                }
            }
        }
        return START_STICKY
    }

    fun getStatus(avgHeart: Int, stepChanges: Int): Int {
        var status = 0
        if (stepChanges == 0) {
            if (avgHeart < minLimit) {
                status = 3
            } else if (avgHeart > maxStillLimit){
                status = 1
            }
        } else {
            if (avgHeart < minLimit) {
                status =  4
            } else if (avgHeart > maxWalkLimit){
                status =  2
            }
        }
        return status
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
                        if (heartRate != null && heartRate > 0) {
                            heartList.add(heartRate)
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

    private fun getStep() {
        val basicService = BLE.bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
        val stepLevel = basicService?.getCharacteristic(UUIDs.BASIC_STEP_CHARACTERISTIC)
        if (stepLevel != null) {
            bluetoothService?.readCharacteristic(stepLevel)
        }
    }

    private fun sendData(avgHeart: Int, stepChanges: Int, step: Int, label: String) {
        stepList.clear()
        heartList.clear()
        scope.launch {
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.now().format(format)
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
            useCase.addData(useCase.getBearer().first().toString(), avgHeart, stepChanges, step, label, date).collect {
                when (it) {
                    is Resource.Success -> {
                        useCase.deleteMonitoringDataByDate(date)
                    }
                    else -> {}
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
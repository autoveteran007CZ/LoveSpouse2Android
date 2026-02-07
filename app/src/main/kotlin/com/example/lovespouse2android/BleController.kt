package com.example.lovespouse2android

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build

class BleController(context: Context) {
    private val bluetoothManager: BluetoothManager? = 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(BluetoothManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        }
    
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val manufacturerDataList = arrayOf(
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE5.toByte(), 0x15.toByte(), 0x7D.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE4.toByte(), 0x9C.toByte(), 0x6C.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE7.toByte(), 0x07.toByte(), 0x5E.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE6.toByte(), 0x8E.toByte(), 0x4F.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE1.toByte(), 0x31.toByte(), 0x3B.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE0.toByte(), 0xB8.toByte(), 0x2A.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE3.toByte(), 0x23.toByte(), 0x18.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xE2.toByte(), 0xAA.toByte(), 0x09.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xED.toByte(), 0x5D.toByte(), 0xF1.toByte()),
        byteArrayOf(0x6D.toByte(), 0xB6.toByte(), 0x43.toByte(), 0xCE.toByte(), 0x97.toByte(), 0xFE.toByte(), 0x42.toByte(), 0x7C.toByte(), 0xEC.toByte(), 0xD4.toByte(), 0xE0.toByte())
    )

    fun sendAdvertisement(modeIndex: Int) {
        if (bluetoothAdapter == null) {
            return // Bluetooth není dostupný
        }
        // TODO: Implementace BLE advertising
    }

    fun sendBPMMode(modeIndex: Int, onMs: Long, offMs: Long) {
        if (bluetoothAdapter == null) {
            return
        }
        // TODO: Implementace BPM režimu
    }

    fun stop() {
        sendAdvertisement(0) // Index 0 = STOP
    }
}

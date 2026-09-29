package com.orbit.recovery

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat

class OrbitVpnService : VpnService() {
    private var pfd: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") { 
            stopVpn()
            return START_NOT_STICKY 
        }
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        if (pfd != null) return
        
        try {
            val builder = Builder()
                .setSession("Orbit Shield")
                .addAddress("10.0.0.2", 32)
                .addDnsServer("185.228.168.9")
                .addDnsServer("185.228.169.9")
                .addRoute("0.0.0.0", 0)
                .setBlocking(false)
            
            pfd = builder.establish()
            startForeground(1003, buildNotification())
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun stopVpn() {
        try {
            pfd?.close()
            pfd = null
        } catch (e: Exception) {}
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("orbit_vpn", "Content Filter", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        
        return NotificationCompat.Builder(this, "orbit_vpn")
            .setContentTitle("Orbit Shield is active 🛡️")
            .setContentText("Blocking adult & harmful content")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}

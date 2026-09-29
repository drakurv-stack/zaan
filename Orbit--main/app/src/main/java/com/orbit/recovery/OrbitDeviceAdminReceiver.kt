package com.orbit.recovery

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class OrbitDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        val prefs = context.getSharedPreferences("orbit_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("pending_deactivation", true).apply()
        Toast.makeText(context, "Please enter your Orbit unlock PIN to disable protection.", Toast.LENGTH_LONG).show()
        return "Please enter your Orbit unlock PIN to disable protection."
    }
}

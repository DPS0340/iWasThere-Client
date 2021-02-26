package xyz.dps0340.iwasthere

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

object LocationHelper {
    val defaultUpdateArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET)
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(activity: AppCompatActivity, client: FusedLocationProviderClient, callback: LocationCallback, permissions: Array<String> = defaultUpdateArray, permissionCode: Int = PermissionCode.default, priority: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) {
        PermissionHelper.ensurePermissions(activity, permissions, permissionCode)
        val locationRequest = LocationRequest().setPriority(priority)
        client.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper())
    }
}
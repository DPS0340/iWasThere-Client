package xyz.dps0340.iwasthere

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

object LocationHelper {
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(activity: AppCompatActivity, client: FusedLocationProviderClient, callback: LocationCallback, permissionCode: Int = PermissionCode.default, priority: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) {
        PermissionHelper.ensurePermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
        val locationRequest = LocationRequest().setPriority(priority)
        client.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper())
    }
}
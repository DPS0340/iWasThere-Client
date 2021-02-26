package xyz.dps0340.iwasthere

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object PermissionHelper {
    fun ensurePermissions(activity: AppCompatActivity, permissions: Array<String>, permissionCode: Int): Boolean {
        val filteredPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (filteredPermissions.isEmpty()) {
            return true
        }
        activity.requestPermissions(filteredPermissions, permissionCode)
        return true
    }
}
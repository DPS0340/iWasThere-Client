package xyz.dps0340.iwasthere.ui.home

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import xyz.dps0340.iwasthere.LocationHelper
import xyz.dps0340.iwasthere.MainActivity

class HomeViewModel : ViewModel(), View.OnClickListener {
    val label = "GoogleMap"
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    override fun onClick(p0: View?) {
        val mainActivity = MainActivity.instance
        val client = mainActivity.fusedLocationClient
        LocationHelper.startLocationUpdates(mainActivity, client, locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                location?.let {
                    val lat = it.latitude
                    val long = it.longitude
                    Log.i(label, LatLng(lat, long).toString())
                    _text.apply {
                        value = "latitude: $lat | longitude: $long"
                    }
                }
            }
        }
    }
}
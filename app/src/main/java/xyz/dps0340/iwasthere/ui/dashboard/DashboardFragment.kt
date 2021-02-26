package xyz.dps0340.iwasthere.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import xyz.dps0340.iwasthere.LocationHelper
import xyz.dps0340.iwasthere.MainActivity
import xyz.dps0340.iwasthere.R


class DashboardFragment : Fragment(), OnMapReadyCallback {
    lateinit var googleMap: GoogleMap
    lateinit var mainActivity: MainActivity
    lateinit var client: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    val history = mutableListOf<LatLng>()
    val label = "GoogleMap"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val mapView = view!!.findViewById<MapView>(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        mainActivity = MainActivity.instance
        client = mainActivity.fusedLocationClient
        locationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                var counter = 0
                Log.i(label, locationResult.locations.toString())
                val mapped = locationResult.locations.map{ it -> LatLng(it.latitude, it.longitude) }
                history.addAll(mapped)
                for (latLng in mapped) {
                    latLng.let {
                        counter++
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title("Marker $counter")
                        )
                        Log.i(label, latLng.toString())
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0F))
                    }
                }
                googleMap.addPolyline(
                    PolylineOptions()
                        .clickable(true)
                        .addAll(history)
                )
            }
        }
        return view
    }

    private fun update() {
        LocationHelper.startLocationUpdates(mainActivity, client, locationCallback)
    }

    private fun recursiveUpdate(delayMillis: Long = 5000L) {
        Handler().postDelayed({
            update()
            Handler().postDelayed({
                recursiveUpdate()
            }, delayMillis)
        }, delayMillis)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        this.googleMap = googleMap
        Log.i(label, "onMapReady Called!")
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings?.isMyLocationButtonEnabled = true
        update()
        recursiveUpdate()
    }

}

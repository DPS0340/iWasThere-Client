package xyz.dps0340.iwasthere.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import xyz.dps0340.iwasthere.LocationHelper
import xyz.dps0340.iwasthere.MainActivity
import xyz.dps0340.iwasthere.R
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment(), OnMapReadyCallback {
    lateinit var googleMap: GoogleMap
    lateinit var mainActivity: MainActivity
    lateinit var client: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var placesClient: PlacesClient
    val history = mutableListOf<LatLng>()
    val label = "GoogleMap"
    private var likelyPlaceName: String? = null
    private var likelyPlaceAddress: String?  = null
    private var likelyPlaceAttribution: List<*>? = null
    private var likelyPlaceLatLng: LatLng? = null

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
        val info: ApplicationInfo = mainActivity.packageManager.getApplicationInfo(
            mainActivity.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = info.metaData
        val api_key = bundle.getString("com.google.android.geo.API_KEY")!!
        Places.initialize(mainActivity.applicationContext, api_key)
        placesClient = Places.createClient(mainActivity)
        locationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult?) {
                plot(locationResult)

                val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                val request = FindCurrentPlaceRequest.newInstance(placeFields)
                val placeResult = placesClient.findCurrentPlace(request)
                placeResult.addOnCompleteListener { task ->
                    placeResultListener(task)
                }
            }
        }
        return view
    }

    private fun placeResultListener(task: Task<FindCurrentPlaceResponse>) {
        if (task.isSuccessful && task.result != null) {
            val likelyPlaces = task.result
            if((likelyPlaces?.placeLikelihoods ?: emptyList()).isNotEmpty()) {
                val placeLikelihood = likelyPlaces.placeLikelihoods.get(0)
                likelyPlaceName = placeLikelihood.place.name
                likelyPlaceAddress = placeLikelihood.place.address
                likelyPlaceAttribution = placeLikelihood.place.attributions
                likelyPlaceLatLng = placeLikelihood.place.latLng
            }
            Log.i(label, likelyPlaceName.toString())
            Log.i(label, likelyPlaceAddress.toString())
            Log.i(label, likelyPlaceAttribution.toString())
            Log.i(label, likelyPlaceLatLng.toString())

            // Show a dialog offering the user the list of likely places, and add a
            // marker at the selected place.
            showPlace()
        } else {
            Log.e(label, "Exception: %s", task.exception)
        }
    }

    private fun plot(locationResult: LocationResult?) {
        locationResult ?: return
        Log.i(label, locationResult.locations.toString())
        val mapped = locationResult.locations.map{ it -> LatLng(it.latitude, it.longitude) }
        history.addAll(mapped)
        for (latLng in mapped) {
            latLng.let {
                val sdf = SimpleDateFormat("yyyy/mm/dd hh:mm:ss")
                val currentDate = sdf.format(Date())
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(currentDate)
                )
                Log.i(label, latLng.toString())
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
            }
        }
        googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(history)
        )
    }

    private fun showPlace() {
        // Ask the user to choose the place where they are now.
        val markerLatLng = likelyPlaceLatLng
        var markerSnippet = likelyPlaceAddress
        val sdf = SimpleDateFormat("yyyy/mm/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        if (likelyPlaceAttribution != null) {
            markerSnippet = """
                $markerSnippet
                $likelyPlaceAttribution
                """.trimIndent()
        }

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        googleMap.addMarker(
            MarkerOptions()
                .title("$likelyPlaceName at $currentDate\n")
                .position(markerLatLng!!)
                .snippet(markerSnippet)
        )

        // Position the map's camera at the location of the marker.
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                markerLatLng,
                DEFAULT_ZOOM
            )
        )
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


    companion object {
        private const val DEFAULT_ZOOM = 17.0F
        private const val M_MAX_ENTRIES = 5
    }
}

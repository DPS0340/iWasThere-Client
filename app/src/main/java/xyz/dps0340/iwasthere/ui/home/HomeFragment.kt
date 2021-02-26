package xyz.dps0340.iwasthere.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import splitties.toast.toast
import xyz.dps0340.iwasthere.LocationHelper
import xyz.dps0340.iwasthere.MainActivity
import xyz.dps0340.iwasthere.PermissionHelper
import xyz.dps0340.iwasthere.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val button: Button = root.findViewById(R.id.location_button)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    location?.let {
                        val lat = it.latitude
                        val long = it.longitude
                        textView.text = "latitude: $lat | longitude: $long"
                    }
                }
            }
        }
        button.setOnClickListener {
            val mainActivity = MainActivity.instance
            val client = mainActivity.fusedLocationClient
            LocationHelper.startLocationUpdates(mainActivity, client, locationCallback)
        }
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }




}

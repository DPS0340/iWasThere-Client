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
import androidx.databinding.DataBindingUtil
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
import xyz.dps0340.iwasthere.databinding.FragmentHomeBinding
import xyz.dps0340.iwasthere.databinding.FragmentHomeBinding.inflate

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        binding.viewModel = homeViewModel
        val textView: TextView = binding.root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return binding.root
    }




}

package com.sonicmaster.locationtracker

import android.app.Activity.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sonicmaster.locationtracker.databinding.FragmentLocationBinding
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class LocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentLocationBinding
    private var map: GoogleMap? = null
    private val requestLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                binding.linearLayout.visibility = View.GONE
                binding.mMap.visibility = View.VISIBLE
                GPSUtils(
                    requireContext(), requireActivity()
                ).checkLocationSetting { checkInternet() }
            } else {
                binding.linearLayout.visibility = View.VISIBLE
                binding.mMap.visibility = View.GONE
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mMap.onCreate(savedInstanceState)
            mMap.onResume()

            mMap.getMapAsync(this@LocationFragment)

            grantPermission.setOnClickListener {
                requestLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }


        val isFileCreated: Boolean =
            File(context?.getExternalFilesDir(null), FILE_NAME).createNewFile()

        if (isFileCreated) {
            println("$FILE_NAME created")
        } else {
            println("$FILE_NAME already exists")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (isOnline()) {
            googleMap.let {
                map = it
            }
        } else {
            requireView().toast("Internet is not available")
        }
    }

    private fun getLocation() {
        val myLocationManager = MyLocationManager(requireContext())

        myLocationManager.getUpdatedLocation { location ->
            addMarker(location!!)
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

    }

    private fun addMarker(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng).title("Current Location")

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        map?.addMarker(markerOptions)
    }

    private fun isOnline(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected
    }

    private fun scheduleLocationLogging() {
        val constraints =
            Constraints.Builder().setRequiresBatteryNotLow(false).setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED).build()

        val workerRequest =
            PeriodicWorkRequest.Builder(
                LocationWorker::class.java,
                15,
                TimeUnit.MINUTES
            ).setConstraints(constraints).build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                "locationLog",
                ExistingPeriodicWorkPolicy.REPLACE,
                workerRequest
            )
    }

    private fun checkInternet() {
        if (isOnline()) {
            getLocation()
            scheduleLocationLogging()
            binding.linearLayout.visibility = View.GONE
            binding.mMap.visibility = View.VISIBLE
        } else {
            requireView().toast("Internet is not available")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            101 -> {
                when (resultCode) {
                    RESULT_OK -> {
                        checkInternet()
                    }
                    RESULT_CANCELED -> {
                        GPSUtils(
                            requireContext(), requireActivity()
                        ).checkLocationSetting { checkInternet() }
                    }
                }
            }
        }
    }
}
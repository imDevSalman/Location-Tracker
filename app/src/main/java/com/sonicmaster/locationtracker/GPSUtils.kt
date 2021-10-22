package com.sonicmaster.locationtracker

import android.content.Context
import android.location.LocationManager
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class GPSUtils(context: Context, activity: FragmentActivity) {
    private val mContext: Context = context
    private val mActivity = activity
    private val TAG = "GPS"

    private var settingsClient: SettingsClient? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null

    private var locationManager: LocationManager? = null
    private var locationRequest: LocationRequest? = null

    init {
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        settingsClient = LocationServices.getSettingsClient(mContext)
        locationRequest = LocationRequest.create()

        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 1000
        locationRequest?.fastestInterval = 500

        if (locationRequest != null) {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest!!)
            locationSettingsRequest = builder.build()
        }
    }

    fun checkLocationSetting(block: () -> Unit) {
        locationRequest = LocationRequest.create()
        locationRequest?.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(mContext)
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                block.invoke()
            } catch (e: ApiException) {

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            mActivity,
                            101
                        )
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }
}
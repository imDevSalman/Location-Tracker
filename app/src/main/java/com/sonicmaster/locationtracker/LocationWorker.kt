package com.sonicmaster.locationtracker

import android.content.Context
import android.location.Location
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.DateFormat
import java.util.*

class LocationWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val locationManager = MyLocationManager(context = context)
        locationManager.getUpdatedLocation { location ->
            logLocation(location!!)
        }
        return Result.success()
    }

    private fun logLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        println("current location $latLng")
        try {
            val file = File(context.getExternalFilesDir(""), FILE_NAME)
            val fos = FileOutputStream(file, true)
            fos.write(
                "${
                    DateFormat.getDateTimeInstance().format(Date().time)
                } --> $latLng\n".toByteArray()
            )
            println("Writing finished")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
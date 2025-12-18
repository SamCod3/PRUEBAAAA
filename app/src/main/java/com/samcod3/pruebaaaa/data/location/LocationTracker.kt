package com.samcod3.pruebaaaa.data.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationTracker @Inject constructor(
    private val application: Application
) {
    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    @SuppressLint("MissingPermission") // Se gestiona en la UI antes de llamar
    suspend fun getCurrentLocation(): Location? {
        // Primero intentamos la última conocida (rápido)
        return suspendCancellableCoroutine { cont ->
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(location)
                } else {
                    // Si no hay última, pedimos una fresca (más lento pero preciso)
                    val cancellationToken = CancellationTokenSource()
                    client.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cancellationToken.token
                    ).addOnSuccessListener { currentLocation ->
                        cont.resume(currentLocation)
                    }.addOnFailureListener {
                        cont.resume(null)
                    }
                    
                    cont.invokeOnCancellation { 
                        cancellationToken.cancel() 
                    }
                }
            }.addOnFailureListener {
                cont.resume(null)
            }
        }
    }
}

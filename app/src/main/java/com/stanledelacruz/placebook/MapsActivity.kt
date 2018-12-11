package com.stanledelacruz.placebook

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlacePhotoMetadata
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.stanledelacruz.placebook.adapter.BookMarkInfoWindowAdapter

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocationClient()
        setupGoogleClient()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(BookMarkInfoWindowAdapter(this))
        getCurrentLocation()
        mMap.setOnPoiClickListener {
            displayPoi(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Log.i(TAG, "Location permission denied.")
            }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.e(TAG, "Google play connection failed: " + result.errorMessage)
    }

    private fun setupGoogleClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Places.GEO_DATA_API)
            .build()
    }

    private fun setupLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
        } else {
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                if(it.result != null) {
                    val latLong = LatLng(it.result!!.latitude, it.result!!.longitude)
                    val zoomLevel = 16.0f
                    val update = CameraUpdateFactory.newLatLngZoom(latLong, zoomLevel)
                    mMap.moveCamera(update)
                }
            }
        }
    }

    private fun  displayPoi(pointOfInterest: PointOfInterest) {
        displayPoiGetPlaceStep(pointOfInterest)
    }

    private fun displayPoiGetPlaceStep(pointOfInterest: PointOfInterest) {
        Places.GeoDataApi.getPlaceById(googleApiClient, pointOfInterest.placeId)
            .setResultCallback { places ->
                if (places.status.isSuccess && places.count > 0) {
                    val place = places.get(0).freeze()
                    displayPoiGetPhoto(place)
                } else {
                    Log.e(TAG, "Error with getPlaceById ${places.status.statusMessage}")
                }
                places.release()
            }
    }

    private fun displayPoiGetPhoto(place: Place) {
        Places.GeoDataApi.getPlacePhotos(googleApiClient, place.id)
            .setResultCallback { result ->
                if (result.status.isSuccess) {
                    val photoDataBuffer = result.photoMetadata

                    if (photoDataBuffer.count > 0) {
                        val photo = photoDataBuffer.get(0).freeze()
                        displayPoiGetPhotoStep(place, photo)
                    }
                    photoDataBuffer.release()
                }
            }
    }

    private fun displayPoiGetPhotoStep(place: Place, photo: PlacePhotoMetadata) {
        photo.getScaledPhoto(googleApiClient,
            resources.getDimensionPixelSize(R.dimen.default_image_width),
            resources.getDimensionPixelSize(R.dimen.default_image_height))
            .setResultCallback { result ->
                if (result.status.isSuccess) {
                    val image = result.bitmap
                    displayPoiDisplayStep(place, image)
                } else {
                    displayPoiDisplayStep(place, null)
                }
            }
    }

    private fun displayPoiDisplayStep(place: Place, photo: Bitmap?) {
        val iconPhoto = if (photo == null) {
            BitmapDescriptorFactory.defaultMarker()
        } else {
            BitmapDescriptorFactory.fromBitmap(photo)
        }

        val marker = mMap.addMarker(MarkerOptions()
            .position(place.latLng)
            .title(place.name as? String)
            .snippet(place.phoneNumber as String?)
        )
        marker.tag = photo
    }
}

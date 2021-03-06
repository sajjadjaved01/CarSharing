package com.cardee.renter_book_car.collection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.support.annotation.DrawableRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.cardee.R
import com.cardee.mvp.BaseView
import com.cardee.owner_car_details.service.FetchAddressService
import com.cardee.service.delegate.HideAnimationDelegate
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_collection_area.*

class CollectionAreaActivity : AppCompatActivity(), BaseView, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val PERMISSIONS_REQUEST_ACCESS_LOCATION = 101
    private val ADDRESS_BY_LOCATION_CODE = 201
    private val MY_ADDRESS_BY_LOCATION_CODE = 202
    private val MY_ADDRESS_BY_LOCATION_UPDATE_CODE = 203
    private val SEARCH_ADDRESS_REQUEST_CODE = 204
    private val KEY_CAMERA_POSITION = "camera_position"
    private val KEY_LOCATION = "location"
    private val SINGAPORE_LAT = 1.352083
    private val SINGAPORE_LNG = 103.81983600000001
    private val DEFAULT_ZOOM = 11f
    private val TAG = this.javaClass.simpleName.toString()

    @DrawableRes
    private val myCurrentLocationIcon = R.drawable.ic_my_location
    @DrawableRes
    private val anyOtherLocationIcon = R.drawable.ic_other_location

    private var mMap: GoogleMap? = null
    private var apiClient: GoogleApiClient? = null
    private var mCurrentToast: Toast? = null
    private var addressReceiver: CollectionAreaActivity.AddressResultReceiver? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentAddressString: String? = null
    private var myCurrentAddressString: String? = null
    private var mLocationPermissionGranted: Boolean = false
    private var mLastKnownLocation: Location? = null
    private var mLastSearchLocation: LatLng? = null
    private var mDefaultLocation: LatLng = LatLng(SINGAPORE_LAT, SINGAPORE_LNG)
    private var mCameraPosition: CameraPosition? = null
    private var currentAddress: String? = null
    private var hideDelegate: HideAnimationDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        setContentView(R.layout.activity_collection_area)
        collectionAreaMap.onCreate(savedInstanceState)
        initToolBar()
        initViews()
        setListeners()
        addressReceiver = AddressResultReceiver(handler)
        hideDelegate = HideAnimationDelegate(addressCard)
    }

    private fun setListeners() {
        myLocationButton.setOnClickListener { moveToMyCurrentLocation() }
        collectionSaveButton.setOnClickListener {
            if (mMap != null) {
                val intent = Intent()
                if (currentAddress == null) {
                    currentAddress = ""
                }
                intent.putExtra("address", currentAddress)
                intent.putExtra("location", mLastSearchLocation)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        addressCard.setOnClickListener {
            if (hideDelegate?.isAnimating() == true) {
                return@setOnClickListener
            }
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .build(this)
                startActivityForResult(intent, SEARCH_ADDRESS_REQUEST_CODE)
                if (hideDelegate?.isHidden == false) {
                    hideDelegate?.hide()
                }
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        collectionAreaMap.onResume()
    }

    private fun initToolBar() {
        setSupportActionBar(collectionAreaToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
    }

    private fun initViews() {
        apiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build()
        apiClient?.connect()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setOnCameraIdleListener {
            val latLng = mMap?.cameraPosition?.target
            requestAddressByLocation(latLng, ADDRESS_BY_LOCATION_CODE)
        }

        getLocationPermission()
        getDeviceLocation()

        val myCurrentLocation = LatLng(mLastKnownLocation?.latitude ?: return,
                mLastKnownLocation?.longitude ?: return)
        requestAddressByLocation(myCurrentLocation, MY_ADDRESS_BY_LOCATION_CODE)
    }

    private fun requestAddressByLocation(location: LatLng?, requestCode: Int) {
        mLastSearchLocation = location
        val requestLocationIntent = Intent(this, FetchAddressService::class.java)
        requestLocationIntent.putExtra(FetchAddressService.LOCATION, location)
        requestLocationIntent.putExtra(FetchAddressService.RECEIVER, addressReceiver)
        requestLocationIntent.putExtra(FetchAddressService.REQUEST_CODE, requestCode)
        this.startService(requestLocationIntent)
    }

    private fun getDeviceLocation() {
        mLastKnownLocation = getLocation()
        when {
            mCameraPosition != null -> mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))
            mLastKnownLocation != null -> mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(mLastKnownLocation?.latitude ?: return,
                            mLastKnownLocation?.longitude ?: return), DEFAULT_ZOOM))
            else -> mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
        }
    }

    private fun getLocation(): Location? {
        var location: Location? = null

        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        var gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!gpsEnabled && !networkEnabled) {
            Log.d(TAG, "getLocation: no provider is enabled")
        } else {
            if (networkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            }
            if (gpsEnabled) {
                if (location == null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        }
        return location
    }

    private fun getLocationPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSIONS_REQUEST_ACCESS_LOCATION)
        }
    }

    private fun moveToMyCurrentLocation() {
        getLocationPermission()
        val location: Location? = getLocation()
        val latLng = LatLng(location?.latitude ?: return, location.longitude)
        updateCurrent(latLng, MY_ADDRESS_BY_LOCATION_UPDATE_CODE)
    }

    private fun updateCurrent(location: LatLng, addressRequestCode: Int) {
        moveMapToLocation(location)
        requestAddressByLocation(location, addressRequestCode)
    }

    private fun moveMapToLocation(location: LatLng) {
        if (mMap == null) {
            Log.e(TAG, "GoogleMap is not instantiated")
            return
        }
        val currentZoom = mMap?.cameraPosition?.zoom
        val position = CameraPosition.Builder()
                .target(location)
                .zoom(if (currentZoom ?: return < 9) 9f else currentZoom)
                .build()
        val focus = CameraUpdateFactory.newCameraPosition(position)
        mMap?.animateCamera(focus)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true
                moveToMyCurrentLocation()
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onConnected(p0: Bundle?) {
        collectionAreaMap.getMapAsync(this)
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onStop() {
        super.onStop()
        apiClient?.disconnect()
        collectionAreaMap.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        collectionAreaMap.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        if (mMap != null) {
            outState?.putParcelable(KEY_CAMERA_POSITION, mMap?.cameraPosition)
            outState?.putParcelable(KEY_LOCATION, mLastKnownLocation)
            super.onSaveInstanceState(outState)
        }
    }

    override fun onLocationChanged(p0: Location?) {

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun showProgress(show: Boolean) {

    }

    override fun showMessage(message: String?) {
        mCurrentToast?.cancel()
        mCurrentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        mCurrentToast?.show()
    }

    override fun showMessage(messageId: Int) {
        showMessage(getString(messageId))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun markAsMyCurrentLocation(myCurrentLocation: Boolean) {
        myLocationButton.setImageResource(if (myCurrentLocation) myCurrentLocationIcon else anyOtherLocationIcon)
    }

    private fun isMyCurrentLocation(): Boolean {
        return currentAddressString != null &&
                myCurrentAddressString != null &&
                currentAddressString.equals(myCurrentAddressString, ignoreCase = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SEARCH_ADDRESS_REQUEST_CODE) {
            if (hideDelegate?.isHidden == true) {
                hideDelegate?.show()
            }
            if (resultCode == RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(this, data)
                updateCurrent(place.latLng, ADDRESS_BY_LOCATION_CODE)
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(this, data)
                Log.e(TAG, status.statusMessage)
            }
        }
    }

    private inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultCode == FetchAddressService.CODE_SUCCESS) {
                val address = resultData.getParcelable<Address>(FetchAddressService.ADDRESS)
                val requestCode = resultData.getInt(FetchAddressService.REQUEST_CODE)
                val addressString = address.thoroughfare ?: address.getAddressLine(0)
                currentAddress = addressString
                when (requestCode) {
                    MY_ADDRESS_BY_LOCATION_UPDATE_CODE -> {
                        myCurrentAddressString = addressString
                        addressText.text = addressString
                        currentAddressString = addressString
                    }
                    ADDRESS_BY_LOCATION_CODE -> {
                        addressText.text = addressString
                        currentAddressString = addressString
                    }
                    MY_ADDRESS_BY_LOCATION_CODE -> {
                        myCurrentAddressString = addressString
                    }
                }
            }
            markAsMyCurrentLocation(isMyCurrentLocation())
        }
    }
}
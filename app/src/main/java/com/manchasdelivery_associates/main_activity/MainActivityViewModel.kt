package com.manchasdelivery.main_activity

import android.annotation.SuppressLint
import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.manchasdelivery_associates.utils.GeneralStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(private val firebaseUser: FirebaseUser?,
                            private val app: Application): ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _navigateToProfileFragment = MutableLiveData<Boolean>()
    val navigateToProfileFragment: LiveData<Boolean>
    get() = _navigateToProfileFragment

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        .build()
    private lateinit var connectivityManager: ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _networkCheck.postValue(GeneralStatus.success)
            Log.i("MyStatus","available")
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _networkCheck.postValue(GeneralStatus.error)
            Log.i("MyStatus","Unavailable")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _networkCheck.postValue(GeneralStatus.error)
            Log.i("MyStatus","Lost")
        }
    }

    private val _networkCheck= MutableLiveData<GeneralStatus>()
    val networkCheck: LiveData<GeneralStatus>
        get() = _networkCheck

    init {
        checkIfIsConnectedToNetwork()
    }

    fun setNavigateToProfileFragment(value: Boolean){
        _navigateToProfileFragment.value = value
    }

    @SuppressLint("MissingPermission")
    fun checkIfIsConnectedToNetwork(){
        connectivityManager = app.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        _networkCheck.value = GeneralStatus.loading
    }

    fun setSplashScreenLoadingStatus(status: Boolean){
        _isLoading.value = status
    }



}
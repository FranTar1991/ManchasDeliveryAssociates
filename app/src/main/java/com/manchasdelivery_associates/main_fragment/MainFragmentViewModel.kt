package com.manchasdelivery_associates.main_fragment

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.database.DatabaseReference
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.utils.*
import kotlinx.coroutines.launch

class MainFragmentViewModel(private val app: Application, private val repo: MainFragmentRepository): AndroidViewModel(app) {


    private val _navigateToProfileFragment = MutableLiveData<Boolean>()
    val navigateToProfileFragment: LiveData<Boolean>
    get() = _navigateToProfileFragment

    private val _isLocationPermissionGranted = MutableLiveData<Boolean>()
    val isLocationPermissionGranted: LiveData<Boolean>
        get() = _isLocationPermissionGranted

    private var _userName  = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _pendingRequestInServer = MutableLiveData<RemoteRequest>()
    val pendingRequestInServer: LiveData<RemoteRequest>
    get() = _pendingRequestInServer

    private val _requestDetails = MutableLiveData<RemoteRequestWithDetails?>()
    val requestDetails: LiveData<RemoteRequestWithDetails?>
        get() = _requestDetails

    private val _callBack = MutableLiveData<STATUSES>()
    val callBack: LiveData<STATUSES>
    get() = _callBack

    private val _isChecked = MutableLiveData<Boolean>()
    val isChecked: LiveData<Boolean>
    get() = _isChecked

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn

    private val _requestStatusChanged = MutableLiveData<String>()
    val requestStatusChanged: LiveData<String>
        get() = _requestStatusChanged


    init {
        listenForPendingRequests()
        _userName.value =  getStringFromSharedPreferences(app,app.getString(R.string.user_name))
        _isUserLoggedIn.value = false

    }

    fun setRequestStatusChanged(value: String){
        _requestStatusChanged.value = value
    }
    fun setIsChecked(value: Boolean){
        _isChecked.value = value
    }
    private fun listenForPendingRequests() {
        viewModelScope.launch {
            repo.listenForPendingRequests(_pendingRequestInServer)
        }
    }

     fun listenForPendingRequestDetails(reference: DatabaseReference, requestId: String){
        viewModelScope.launch {
            repo.listenForPendingRequestDetails(reference, _requestDetails, requestId)
        }
    }

     fun setServerInDb(reference: DatabaseReference){
         val server = MDServer()
            repo.setServerInDb(server, reference, _callBack)

    }

    fun checkServerStatusInDb(reference: DatabaseReference){
        repo.checkServerStatusInDb(reference, _isUserLoggedIn)
    }

    fun setIsLocationPermissionGranted(result: Boolean){
        _isLocationPermissionGranted.value = result
    }


    fun setNavigateToProfileFragment(value: Boolean) {
        _navigateToProfileFragment.value = value
    }

    fun changeRequestStatus(
        requestInUserNodeRef: DatabaseReference,
        requestInServerNodeRef: DatabaseReference,
        status: String
    ) {
        repo.changeRequestStatus(requestInUserNodeRef, requestInServerNodeRef, status, _requestStatusChanged)
    }


}
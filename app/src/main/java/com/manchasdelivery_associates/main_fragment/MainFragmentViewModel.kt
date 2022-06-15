package com.manchasdelivery_associates.main_fragment

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val _callBackForSignInRequest = MutableLiveData<STATUSES>()
    val callBackForSignInRequest: LiveData<STATUSES>
    get() = _callBackForSignInRequest


    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn

    private val _requestStatusChanged = MutableLiveData<String?>()
    val requestStatusChanged: LiveData<String?>
        get() = _requestStatusChanged

    private val _priceChanged = MutableLiveData<Double?>()
    val priceChanged: LiveData<Double?>
        get() = _priceChanged

    private val _openWhatsappChatWithNumber= MutableLiveData<String?>()
    val openWhatsappChatWithNumber: LiveData<String?>
        get() = _openWhatsappChatWithNumber

    private val _registrationToken = MutableLiveData<String?>()
    val registrationToken: LiveData<String?>
        get() = _registrationToken

    init {
        listenForPendingRequests()
        _userName.value =  getStringFromSharedPreferences(app,app.getString(R.string.user_name))
        _isUserLoggedIn.value = false
        setCallBackForSignInRequest(STATUSES.loading)
    }

    fun setCallBackForSignInRequest(value: STATUSES){
        _callBackForSignInRequest.value = value
    }

    fun setRequestStatusChanged(value: String?){
        _requestStatusChanged.value = value
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

    fun setCurrentRequestDetails(value: RemoteRequestWithDetails?){
        _requestDetails.value = value
    }

    fun setOpenWhatsappChatWithNumber(value: String?){
        _openWhatsappChatWithNumber.value = value
    }

     fun setServerInDb(server: MDServer, reference: DatabaseReference){
            repo.setServerInDb(server, reference, _callBackForSignInRequest, _isUserLoggedIn)
    }

    fun setServerStatusListeners(reference: DatabaseReference){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                launch {
                    repo.setServerStatusListeners(reference, _isUserLoggedIn, _callBackForSignInRequest)
                }
            }

        }

    }

    fun setIsLocationPermissionGranted(result: Boolean){
        _isLocationPermissionGranted.value = result
    }


    fun setNavigateToProfileFragment(value: Boolean) {
        _navigateToProfileFragment.value = value
    }

    fun changeRequestStatus(
        requestInUserNodeRef: DatabaseReference,
        status: String?
    ) {
        repo.changeRequestStatus(requestInUserNodeRef, status, _requestStatusChanged)
    }

    fun updatePriceInUserNode(price: Double, requestInUserNodeRef: DatabaseReference){
        repo.updatePriceInUserNodeRef(requestInUserNodeRef, price, _priceChanged)
    }

     fun getNewToken(){
        // Get token
        // [START log_reg_token]
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            _registrationToken.value = token

        })
        // [END log_reg_token]
    }

}
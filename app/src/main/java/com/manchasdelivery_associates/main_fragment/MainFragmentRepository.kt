package com.manchasdelivery_associates.main_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.manchasdelivery_associates.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.System.currentTimeMillis

class MainFragmentRepository(private val finishedRequestsRef:DatabaseReference,
                             private val requestInServerNodeRef: DatabaseReference,
                             private val thisServerRef: DatabaseReference) {

    suspend fun listenForPendingRequests(_pendingRequest: MutableLiveData<RemoteRequest>) {
        withContext(Dispatchers.IO){
            requestInServerNodeRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    _pendingRequest.postValue(snapshot.getValue(RemoteRequest::class.java))
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

     fun setServerInDb(
         server: MDServer,
         reference: DatabaseReference,
         callback: MutableLiveData<STATUSES>,
         _isUserLoggedIn: MutableLiveData<Boolean>, ) {

         callback.postValue(STATUSES.loading)

             reference.get().addOnSuccessListener { snap->
                 if (snap.value != null){
                     snap.ref.removeValue().addOnSuccessListener {
                         callback.postValue(STATUSES.idle)
                         _isUserLoggedIn.postValue(false)
                     }
                 } else {
                     reference.setValue(server).addOnSuccessListener {
                         callback.postValue(STATUSES.idle)
                         _isUserLoggedIn.postValue(true)
                     }
                 }
             }

    }

    fun setServerStatusListeners(
        reference: DatabaseReference,
        isLogedInCallback: MutableLiveData<Boolean>,
        _callBackForSignInRequest: MutableLiveData<STATUSES>
    ){

        reference.get().addOnSuccessListener {

            if (it.value == null){
                isLogedInCallback.postValue(false)
            }else{

               isLogedInCallback.postValue(true)
            }

            _callBackForSignInRequest.postValue(STATUSES.idle)
        }

        reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key != "current" && isLogedInCallback.value == false){
                    isLogedInCallback.postValue(true)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.key != "current"&& isLogedInCallback.value == true){
                    isLogedInCallback.postValue(false)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    suspend fun listenForPendingRequestDetails(reference:DatabaseReference,
                                               _requestDetails: MutableLiveData<RemoteRequestWithDetails?>,
                                               requestId: String) {
        withContext(Dispatchers.IO){

            reference.addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    parseTheRequest(snapshot,requestId,_requestDetails)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    parseTheRequest(snapshot,requestId,_requestDetails)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    private fun parseTheRequest(snapshot: DataSnapshot, requestId: String,
                                livedata: MutableLiveData<RemoteRequestWithDetails?>){
        val newRequestDetails = snapshot.getValue(RemoteRequestWithDetails::class.java)
        if (snapshot.key == requestId && !checkIfIsLocationUpdate(livedata.value, newRequestDetails)){
            livedata.postValue(newRequestDetails)
        }
    }

    private fun checkIfIsLocationUpdate(currentValue: RemoteRequestWithDetails?, newValue: RemoteRequestWithDetails?): Boolean {
        val isLocationUpdate = currentValue?.locationBAddressLat == newValue?.locationBAddressLat || currentValue?.locationBAddressLong == newValue?.locationBAddressLong

        Log.i("IsLocationUpdate", "$isLocationUpdate")
        return isLocationUpdate
    }

    fun updateLocationOfDelivery(reference:DatabaseReference, latLng: LatLng) {
        val latReference = reference.child("trackingLat")
        val longReference = reference.child("trackingLong")
        latReference.setValue(latLng.latitude)
        longReference.setValue(latLng.longitude)
    }

    fun updatePriceInUserNodeRef( requestInUserNodeRef: DatabaseReference, price: Double?, _priceUpdateCallback: MutableLiveData<Double?>){
        price?.let {
            requestInUserNodeRef.child("price").setValue(price).addOnSuccessListener {
                _priceUpdateCallback.postValue(price)
            }
        }
    }
    fun changeRequestStatus(
        requestInUserNodeRef: DatabaseReference, status: String?, _requestCompleteCallback: MutableLiveData<String?>
    ) {
        requestInServerNodeRef.get().addOnSuccessListener {
            it?.ref?.removeValue()
            thisServerRef.child("lastTimeUsed").setValue(currentTimeMillis())
        }

        status?.let {
            requestInUserNodeRef.child("status").setValue(status).addOnSuccessListener {
                _requestCompleteCallback.postValue(status)
            }
        }

        if (status == STATUS.Finished.name){
            requestInUserNodeRef.get().addOnSuccessListener {
                val requestKey = it.key
                val requestToSave = it.getValue(RemoteRequestWithDetails::class.java)
                requestToSave?.id = requestKey

                finishedRequestsRef.push().setValue(requestToSave)
            }
        }


    }
}
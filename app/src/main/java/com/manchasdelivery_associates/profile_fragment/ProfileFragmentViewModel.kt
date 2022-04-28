package com.manchasdelivery_associates.profile_fragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.utils.getStringFromSharedPreferences

class ProfileFragmentViewModel(private val app: Application, private val firebaseUser: FirebaseUser) : ViewModel() {

    val userPhoneNumber = firebaseUser.phoneNumber
    private var _userName  = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _navigateToProfileFragment = MutableLiveData<Boolean>()
    val navigateToProfileFragment : LiveData<Boolean>
    get() = _navigateToProfileFragment

    init {
        _userName.value =  getStringFromSharedPreferences(app,app.getString(R.string.user_name))
    }

    fun setNavigateToMainFragment(value: Boolean){
        _navigateToProfileFragment.value = value
    }

}
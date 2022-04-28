package com.manchasdelivery_associates.profile_fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ProfileFragmentViewModelFactory(private val app: Application, private val firebaseUser: FirebaseUser): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileFragmentViewModel::class.java)){
            return ProfileFragmentViewModel(app, firebaseUser) as T
        }

        throw IllegalArgumentException("Unknown model class")
    }
}
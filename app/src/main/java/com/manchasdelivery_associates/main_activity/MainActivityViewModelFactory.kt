package com.manchasdelivery.main_activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import java.lang.IllegalArgumentException

@Suppress("unchecked_cast")
class MainActivityViewModelFactory(
    private val fireBaseUser: FirebaseUser?,
    private val app: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel(fireBaseUser, app) as T
        }
        throw IllegalArgumentException("Unknown model class")
    }
}
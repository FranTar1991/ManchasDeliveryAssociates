package com.manchasdelivery_associates.main_fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class MainFragmentViewModelFactory(private val app: Application , private val repo: MainFragmentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainFragmentViewModel::class.java)){
            return MainFragmentViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unknown model class")
    }
}
package com.manchasdelivery_associates.main_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.manchasdelivery.main_activity.MainActivityViewModel
import com.manchasdelivery.main_activity.MainActivityViewModelFactory
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.databinding.ActivityMainBinding
import com.manchasdelivery_associates.main_fragment.MainFragmentDirections
import com.manchasdelivery_associates.utils.createSignInIntent
import com.manchasdelivery_associates.utils.sendRegistrationToServer

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var database: DatabaseReference
    private val signInLauncher= registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        database = Firebase.database.reference
        val factory = MainActivityViewModelFactory(firebaseUser, application)
        viewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        addInitialDataListener(viewModel)


        if (firebaseUser == null){
            createSignInIntent(signInLauncher)
            viewModel.setSplashScreenLoadingStatus(true)
        }



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        viewModel.navigateToProfileFragment.observe(this, Observer { flag ->

            if (flag){
                navController
                   .navigate(MainFragmentDirections.actionMainFragmentToProfileFragment())
            }
        })


    }



    private fun addInitialDataListener(viewModel: MainActivityViewModel) {
        // Set up an OnPreDrawListener to the root view.
        val content: View? = findViewById(android.R.id.content)
        content?.viewTreeObserver?.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (viewModel.isLoading.value) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            viewModel.setNavigateToProfileFragment(true)

            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            if (response == null){
                finish()
            } else{
                Toast.makeText(this,getString(R.string.error_log_in, response.error?.errorCode),
                    Toast.LENGTH_SHORT).show()
            }
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
        viewModel.setSplashScreenLoadingStatus(false)
    }


}
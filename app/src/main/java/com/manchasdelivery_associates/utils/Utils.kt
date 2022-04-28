package com.manchasdelivery_associates.utils


import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock

import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.main_activity.MainActivity
import java.lang.System.currentTimeMillis


enum class STATUSES{
    success, error, loading
}

fun showSnackbar(view: View, text: String){
    Snackbar.make(view,text, Snackbar.LENGTH_SHORT).show()
}

 fun createSignInIntent(signInLauncher: ActivityResultLauncher<Intent>) {
    // [START auth_fui_create_intent]
    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.PhoneBuilder().build())

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()
    signInLauncher.launch(signInIntent)
    // [END auth_fui_create_intent]
}
fun saveStringToSharedPreferences(key: String, value: String, context: Context
) {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    with (sharedPref.edit()) {
        putString(key, value)
        apply()
    }
}

fun cancelNotification(context: Context?) {
    val notificationManager =context?.
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(NotificationUtils.N_ID_F_ScreenShot)
}

fun getStringFromSharedPreferences(context: Context, key: String, defaultValue: String? = ""): String? {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    return sharedPref?.getString(key,defaultValue)
}

val ACTION_PROCESS_UPDATES =
    "com.google.android.gms.location.sample.locationupdatespendingintent.action" +
            ".PROCESS_UPDATES"

fun checkGps(context: Context?): Boolean{
    val mLocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

enum class STATUS(){
    Received,Progress,Finished, Canceled, Unknown
}

fun showAlertDialog(
    title: String,
    message: String,
    activity: Activity?,
    hasCancelButton: Boolean = true,
    functionToExecute: (() -> Unit)?
): AlertDialog? {
    val alertDialog: AlertDialog? = activity?.let {
        val builder = AlertDialog.Builder(it)
        builder.apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton(
                R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    functionToExecute?.let { function -> function() }
                    dialog.cancel()
                })
            if (hasCancelButton){
                setNegativeButton(R.string.cacenlIt){dialog, id ->
                    dialog.cancel()
                }
            }

        }
        builder.create()
    }
    return alertDialog
}

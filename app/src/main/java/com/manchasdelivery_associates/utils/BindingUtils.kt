package com.manchasdelivery_associates.utils

import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.model.LatLng
import com.manchasdelivery_associates.R

@BindingAdapter("setTheAddressLat", "setTheAddressLong")
fun TextView.setTheAddress(addressLat: Double?, addressLong: Double?){

    setOnClickListener {

        val gmmIntentUri =
            Uri.parse("geo:0,0?q="+addressLat+","+addressLong+"(Location B Address)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

}

@BindingAdapter("setThePriceText")
fun EditText.setThePriceText(price: String?){
    setText(if (price == context.getString(R.string.to_be_defined)){
        isEnabled = true
        ""
    }else{
        isEnabled = false
        price
    })
}

@BindingAdapter("set_hello_text")
fun TextView.set_hello_text(userName: String?){

    text = context.getString(R.string.hello_text,userName)
}
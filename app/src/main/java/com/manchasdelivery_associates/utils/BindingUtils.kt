package com.manchasdelivery_associates.utils

import android.content.Intent
import android.graphics.Color.red
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.model.LatLng
import com.manchasdelivery_associates.R

@BindingAdapter("setTheAddressLat", "setTheAddressLong","setTheAddressReference")
fun TextView.setTheAddress(addressLat: Double?, addressLong: Double?, addressReference: String?){

    setOnClickListener {

        showAlertDialog(context.getString(R.string.want_launch_map),addressReference.toString(),context,true){
            val gmmIntentUri =
                Uri.parse("geo:0,0?q="+addressLat+","+addressLong+"("+addressReference+")")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }?.show()


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

@BindingAdapter("setEnabled")
fun Button.setEnabled(status: String?){
   isEnabled=  status != STATUS.Canceled.name

}

@BindingAdapter("setVisibility")
fun Button.setVisibility(status: String?){
    isVisible=  status == STATUS.Canceled.name

}

@BindingAdapter("setTheBackground")
fun ConstraintLayout.setTheBackground(status: String?){
    setBackgroundColor( if (status == STATUS.Canceled.name){
        ContextCompat.getColor(context,R.color.red)
    } else{ContextCompat.getColor(context,R.color.white)
    })
}
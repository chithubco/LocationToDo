package com.echithub.locationtodo.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.echithub.locationtodo.R

@BindingAdapter("latLongUnitText")
fun bindingTextViewLatLongUnit(textView: TextView,number: Float){
    val context = textView.context
    textView.text = String.format(context.getString(R.string.latlong_unit_format), number)
}
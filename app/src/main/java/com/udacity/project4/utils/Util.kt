package com.udacity.project4.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.udacity.project4.R

@BindingAdapter("latLongUnitText")
fun bindingTextViewLatLongUnit(textView: TextView,number: Float){
    val context = textView.context
    textView.text = String.format(context.getString(R.string.latlong_unit_format), number)
}
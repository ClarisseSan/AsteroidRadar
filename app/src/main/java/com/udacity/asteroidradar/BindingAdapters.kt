package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.network.PictureOfDay

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.setColorFilter(R.color.potentially_hazardous)
        imageView.contentDescription =
            imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription =
            imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =
            imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription =
            imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, pictureOfDay: PictureOfDay?) {
    pictureOfDay?.let {
        if (it.mediaType == "image") {
            val imgUri = it.url.toUri().buildUpon().scheme("https").build()
            Glide.with(imgView.context)
                .load(imgUri)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .centerCrop()
                .into(imgView)
        } else {
            imgView.setImageResource(R.drawable.placeholder_picture_of_day)
        }
    }
}


@BindingAdapter("asteroidApiStatus")
fun bindAsteroidStatus(statusImageView: ImageView, status: MainViewModel.ApiStatus?) {
    when (status) {
        MainViewModel.ApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }

        MainViewModel.ApiStatus.ERROR -> {
            //statusImageView.visibility = View.VISIBLE
            //statusImageView.setImageResource(R.drawable.ic_connection_error)
        }

        MainViewModel.ApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }


    }
}

/**
 * Binding adapter used to hide the spinner once data is available
 */
@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}
package ru.netology.nmedia.image

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R


fun ImageView.load(url: String ) {
    val requestOptions = RequestOptions.circleCropTransform()
    Glide.with(this)
        .load(url)
        .apply(requestOptions)
        .error(R.drawable.ic_baseline_more_vert_24)
        .timeout(10_000)
        .into(this)
}

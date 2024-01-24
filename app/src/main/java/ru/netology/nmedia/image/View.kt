package ru.netology.nmedia.image

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
fun ImageView.loading(url: String){
    Glide.with(this)
        .load(url)
        .skipMemoryCache(true)
        .error(R.drawable.ic_baseline_more_vert_24)
        .timeout(10_000)
        .into(this)

}

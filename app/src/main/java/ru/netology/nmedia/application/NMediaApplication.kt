package ru.netology.nmedia.application

import android.app.Application
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideModule
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import ru.netology.nmedia.auth.AppAuth
@HiltAndroidApp
class NMediaApplication : Application()

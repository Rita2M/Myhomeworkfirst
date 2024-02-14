package ru.netology.nmedia.modules


import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoogleApiModule {
    @Singleton
    @Provides
    fun googleApiAvail(): GoogleApiAvailability {
        return GoogleApiAvailability.getInstance()

    }
}

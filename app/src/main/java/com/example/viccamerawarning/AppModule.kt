package com.example.viccamerawarning


import android.app.Application
import com.example.viccamerawarning.data.repository.CameraRepository
import com.example.viccamerawarning.location.DefaultLocationTracker
import com.example.viccamerawarning.location.LocationTracker
import com.example.viccamerawarning.tts.ISpeechService
import com.example.viccamerawarning.tts.SpeechService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // available app-wide
object AppModule {

    @Provides
    @Singleton
    fun provideCameraRepository(): CameraRepository {
        return CameraRepository() // your concrete implementation
    }

    @Provides
    @Singleton
    fun provideLocationTracker(app: Application): LocationTracker {
        return DefaultLocationTracker(app)
    }

    @Provides
    @Singleton
    fun provideSpeechService(app: Application): ISpeechService {
        return SpeechService(app)
    }
}
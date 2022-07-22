package me.kz.shizukuactivitymanager

import android.app.Application
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader {
            ImageLoader(this@App).newBuilder()
                .components {
                    add(AppIconFetcherFactory(this@App))
                }
                .interceptorDispatcher(dispatcher)
                .dispatcher(dispatcher)
                .build()
        }
    }
}

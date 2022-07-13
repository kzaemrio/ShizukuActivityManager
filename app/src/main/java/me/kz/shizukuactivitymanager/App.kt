package me.kz.shizukuactivitymanager

import android.app.Application
import android.content.pm.PackageInfo
import coil.Coil
import coil.ImageLoader
import coil.fetch.Fetcher
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
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
                .dispatcher(dispatcher)
                .build()
        }
    }
}

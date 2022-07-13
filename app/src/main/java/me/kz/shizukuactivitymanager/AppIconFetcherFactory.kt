package me.kz.shizukuactivitymanager

import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import me.zhanghai.android.appiconloader.AppIconLoader

class AppIconFetcherFactory(private val context: Context) : Fetcher.Factory<PackageInfo> {

    private val loader = AppIconLoader(40.dp, false, context)

    override fun create(data: PackageInfo, options: Options, imageLoader: ImageLoader): Fetcher {
        return AppIconFetcher(context, data, loader)
    }
}

class AppIconFetcher(
    private val context: Context,
    private val info: PackageInfo,
    private val loader: AppIconLoader
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        return DrawableResult(
            BitmapDrawable(
                context.resources,
                loader.loadIcon(info.applicationInfo)
            ),
            true,
            DataSource.DISK
        )
    }
}

val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

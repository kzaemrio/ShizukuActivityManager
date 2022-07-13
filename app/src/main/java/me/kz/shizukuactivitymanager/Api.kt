package me.kz.shizukuactivitymanager

import android.app.ActivityManagerNative
import android.app.IActivityManager
import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ParceledListSlice
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import javax.inject.Inject

class Api @Inject constructor() {
    private val iActivityManager: IActivityManager = ActivityManagerNative.asInterface(
        ShizukuBinderWrapper(
            SystemServiceHelper.getSystemService(Context.ACTIVITY_SERVICE)
        )
    )

    private val iPackageManager: IPackageManager = IPackageManager.Stub.asInterface(
        ShizukuBinderWrapper(
            SystemServiceHelper.getSystemService("package")
        )
    )

    fun clearApplicationUserData(pgName: String) {
        HiddenApiBypass.invoke(
            IActivityManager::class.java,
            iActivityManager,
            "clearApplicationUserData",
            pgName, false, null, 0
        )
    }

    fun forceStopPackage(pgName: String) {
        iActivityManager.forceStopPackage(pgName, 0)
    }

    @Suppress("UNCHECKED_CAST")
    fun getInstalledPackages(): List<PackageInfo> {
        return HiddenApiBypass.invoke(
            ParceledListSlice::class.java,
            iPackageManager.getInstalledPackages(PackageManager.GET_META_DATA, 0),
            "getList"
        ) as List<PackageInfo>
    }
}

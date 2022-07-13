# ShizukuActivityManager

execute `IActivityManager.forceStopPackage` and `iActivityManager.clearApplicationUserData` 
by [Shizuku](https://github.com/RikkaApps/Shizuku) 
and [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass)

why: some app are blocking clearApplicationUserData function by 
adding `<application android:manageSpaceActivity="com.example.ActivityOfMyChoice">`to manifest file

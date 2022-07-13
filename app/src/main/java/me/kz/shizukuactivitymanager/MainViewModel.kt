package me.kz.shizukuactivitymanager

import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import javax.inject.Inject

private const val TAG = "MainViewModel"

@OptIn(ObsoleteCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher,
    pm: PackageManager,
    private val api: Api
) : ViewModel() {

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh: StateFlow<Boolean>
        get() = _isRefresh

    private val _list = MutableStateFlow(emptyList<Item>())
    val list: StateFlow<List<Item>>
        get() = _list

    private val channel = BroadcastChannel<Action>(1)

    val alertEventFlow = channel.openSubscription().consumeAsFlow().filterIsInstance<AlertAction>()

    init {
        viewModelScope.launch {
            _isRefresh.emit(true)
            val result = withContext(coroutineDispatcher) {
                api.getInstalledPackages().map {
                    Item(it.applicationInfo.loadLabel(pm).toString(), it.packageName, it)
                }
            }
            _list.emit(result)
            _isRefresh.emit(false)
        }
    }

    fun onClear(pgName: String) {
        permission { api.clearApplicationUserData(pgName) }
    }

    fun onStop(pgName: String) {
        permission { api.forceStopPackage(pgName) }
    }

    private fun permission(action: () -> Unit) {
        viewModelScope.launch {
            if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
                action()
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                channel.send(AlertAction("shouldShowRequestPermissionRationale"))
            } else {
                Shizuku.requestPermission(233)
            }
        }
    }
}

sealed interface Action
data class AlertAction(val message: String) : Action

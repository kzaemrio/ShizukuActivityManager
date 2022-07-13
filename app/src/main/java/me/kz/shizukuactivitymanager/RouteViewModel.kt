package me.kz.shizukuactivitymanager

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class RouteViewModel @Inject constructor() : ViewModel() {

    private val _flow: MutableStateFlow<UiState> = MutableStateFlow(Refresh)
    val flow: StateFlow<UiState>
        get() = _flow

    init {
        viewModelScope.launch {
            val state = try {
                onSelfPermission(Shizuku.checkSelfPermission())
            } catch (e: IllegalStateException) {
                Uninstall
            }
            _flow.emit(state)
        }
    }

    private suspend fun onSelfPermission(selfPermission: Int): UiState {
        return when (selfPermission) {
            PackageManager.PERMISSION_GRANTED -> Home
            PackageManager.PERMISSION_DENIED -> PermissionDeny
            else -> onSelfPermission(requestPermission())
        }
    }

    private suspend fun requestPermission(): Int = suspendCoroutine { co ->
        Shizuku.addRequestPermissionResultListener(object :
            Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(
                requestCode: Int,
                grantResult: Int
            ) {
                Shizuku.removeRequestPermissionResultListener(this)
                co.resume(grantResult)
            }
        })
        Shizuku.requestPermission(2333)
    }
}

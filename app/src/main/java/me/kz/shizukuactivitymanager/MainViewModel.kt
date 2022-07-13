package me.kz.shizukuactivitymanager

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val api: Api,
    private val pm: PackageManager,
) : ViewModel() {

    private val _filter = MutableStateFlow("")
    val filter: StateFlow<String>
        get() = _filter

    private val _list = MutableStateFlow(emptyList<Item>())
    val list: StateFlow<List<Item>>
        get() = _list

    init {
        viewModelScope.launch {
            val all = withContext(coroutineDispatcher) {
                api.getInstalledPackages()
                    .asSequence()
                    .map { Item(it.applicationInfo.loadLabel(pm).toString(), it.packageName, it) }
                    .toList()
            }
            combine(filter, flow { emit(all) }) { filterKeyWord, allList ->
                if (filterKeyWord.isBlank()) {
                    allList
                } else {
                    allList.filter {
                        it.appName.contains(filterKeyWord) || it.pgName.contains(filterKeyWord)
                    }
                }
            }.onEach { _list.emit(it) }.launchIn(this)
        }
    }

    fun onFilter(input: String) {
        viewModelScope.launch { _filter.emit(input) }
    }

    fun onClearFilter() {
        viewModelScope.launch { _filter.emit("") }
    }

    fun onClear(pgName: String) {
        api.clearApplicationUserData(pgName)
    }

    fun onStop(pgName: String) {
        api.forceStopPackage(pgName)
    }
}

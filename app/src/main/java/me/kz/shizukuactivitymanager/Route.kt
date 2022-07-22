package me.kz.shizukuactivitymanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun Route(viewModel: RouteViewModel = hiltViewModel()) {
    when (viewModel.flow.collectAsState().value) {
        Refresh -> Refresh()
        Uninstall -> Uninstall()
        PermissionDeny -> PermissionDeny()
        Home -> Home()
    }
}

@Composable
fun Home(viewModel: MainViewModel = hiltViewModel()) {
    Scaffold(topBar = {
        TopAppBar(
            contentPadding = PaddingValues(
                start = 6.dp,
                end = 6.dp,
                top = 6.dp,
                bottom = 10.dp
            )
        ) {
            val focusManager = LocalFocusManager.current
            val text = viewModel.filter.collectAsState().value
            TextField(
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Unspecified,
                    cursorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    placeholderColor = Color.White
                ),
                placeholder = { Text(text = "filter") },
                value = text,
                onValueChange = viewModel::onFilter,
                trailingIcon = {
                    if (text.isNotBlank()) {
                        IconButton(onClick = {
                            viewModel.onClearFilter()
                            focusManager.clearFocus()
                        }) {
                            Icon(imageVector = Icons.Rounded.Clear, contentDescription = "clear")
                        }
                    }
                }
            )
        }
    }) {
        val list: List<Item> by viewModel.list.collectAsState()
        val onClear: (String) -> Unit by rememberUpdatedState(newValue = viewModel::onClear)
        val stop: (String) -> Unit by rememberUpdatedState(newValue = viewModel::onStop)
        Home(
            Modifier.padding(it),
            list,
            onClear,
            stop
        )
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    list: List<Item>,
    onClear: (String) -> Unit,
    onStop: (String) -> Unit
) {
    if (list.isEmpty()) {
        Refresh()
    } else {
        LazyColumn(modifier) {
            items(items = list, key = { it.pgName }) {
                Item(it, onClear, onStop)
            }
        }
    }
}

@Composable
fun Item(
    item: Item,
    onClear: (String) -> Unit,
    onStop: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            modifier = Modifier.size(40.dp),
            model = item.packageInfo,
            contentDescription = item.pgName
        )
        Spacer(modifier = Modifier.width(6.dp))
        Info(item, onClear, onStop)
    }
}

@Composable
fun Info(
    item: Item,
    onClear: (String) -> Unit,
    onStop: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(text = item.appName, maxLines = 1)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = item.pgName)
        Spacer(modifier = Modifier.height(6.dp))
        ButtonGroup(item, onClear, onStop)
    }
}

@Composable
fun ButtonGroup(
    item: Item,
    onClear: (String) -> Unit,
    onStop: (String) -> Unit
) {
    Row(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.weight(1F),
            onClick = { onClear(item.pgName) }
        ) {
            Text(text = "clear")
        }
        Spacer(modifier = Modifier.width(6.dp))
        Button(
            modifier = Modifier.weight(1F),
            onClick = { onStop(item.pgName) }
        ) {
            Text(text = "stop")
        }
    }
}

@Composable
fun PermissionDeny() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "shizuku permission denied")
    }
}

@Composable
fun Uninstall() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "no shizuku app installed or running")
    }
}

@Composable
fun Refresh() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

sealed interface UiState
object Refresh : UiState
object Uninstall : UiState
object PermissionDeny : UiState
object Home : UiState

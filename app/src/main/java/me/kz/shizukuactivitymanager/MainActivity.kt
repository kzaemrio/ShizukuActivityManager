package me.kz.shizukuactivitymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.kz.shizukuactivitymanager.ui.theme.ShizukuActivityManagerTheme

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        setContent {
            ShizukuActivityManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state = rememberScaffoldState()

                    LaunchedEffect(key1 = "init") {
                        viewModel.alertEventFlow.onEach {
                            launch { state.snackbarHostState.showSnackbar(it.message) }
                        }.launchIn(this)
                    }

                    Scaffold(scaffoldState = state) {
                        Home(
                            Modifier.padding(it),
                            viewModel.isRefresh.collectAsState().value,
                            viewModel.list.collectAsState().value,
                            viewModel::onClear,
                            viewModel::onStop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    isRefresh: Boolean,
    list: List<Item>,
    clear: (String) -> Unit,
    stop: (String) -> Unit
) {
    if (isRefresh) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier) {
            items(items = list, key = { it.pgName }) {
                Item(it, clear, stop)
            }
        }
    }
}

@Composable
fun Item(
    item: Item,
    clear: (String) -> Unit,
    stop: (String) -> Unit
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
        Column {
            Text(text = item.appName, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = item.pgName)
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                Button(
                    modifier = Modifier.weight(1F),
                    onClick = { clear(item.pgName) }
                ) {
                    Text(text = "clear")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    modifier = Modifier.weight(1F),
                    onClick = { stop(item.pgName) }
                ) {
                    Text(text = "stop")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShizukuActivityManagerTheme {
        Greeting("Android")
    }
}

package com.funnco.scheduler.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.presentation.theme.SchedulerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SchedulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

}

@Composable
fun Screen(modifier: Modifier) {
    val viewModel : MainActivityViewModel = viewModel()

    Column {
        Text("A")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, viewModel: MainActivityViewModel = viewModel()) {

    val allUsers = viewModel.listOfUsers.collectAsState(emptyList()).value

    Text(
        text = "Hello ${if (allUsers.size == 0) "" else allUsers[0].nickName}!",
        modifier = modifier
    )

}


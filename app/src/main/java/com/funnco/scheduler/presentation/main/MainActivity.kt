package com.funnco.scheduler.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.data.model.UserModel
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel()
) {

    var firstLaunch by remember {
        mutableStateOf(true)
    }
    val allUsers = viewModel.listOfUsers.collectAsState(emptyList()).value

    var selectedUser by remember {
        mutableStateOf(UserModel(-1, ""))
    }

    if(firstLaunch && allUsers.size > 0){
        firstLaunch = false
        selectedUser = allUsers[0]
    }


    var isExpanded by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            TextField(
                value = selectedUser.nickName,
                onValueChange = { isExpanded = !isExpanded },
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = {
                isExpanded = false
            }) {
                allUsers.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.nickName) },
                        onClick = {
                            selectedUser = it
                            isExpanded = false
                        }
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f, true)
                .padding(4.dp)
                .alpha(if (selectedUser.id != -1) 1f else 0f),
            shape = RoundedCornerShape(8.dp)
        ) {

        }

        TextButton(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .alpha(if (selectedUser.id != -1) 1f else 0f)
                .padding(4.dp)
                .height(40.dp)
        ) {
            Text(text = "Изменить расписание ")
        }

        Button(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .padding(4.dp)
                .height(40.dp)
        ) {
            Text(text = "Добавить нового пользователя")
        }
    }


}


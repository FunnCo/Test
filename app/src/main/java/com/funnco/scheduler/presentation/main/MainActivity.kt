package com.funnco.scheduler.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.presentation.fullSchedule.FullScheduleActivity
import com.funnco.scheduler.presentation.theme.SchedulerTheme
import com.funnco.scheduler.presentation.timeEdit.TimeEditActivity

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

    val context = LocalContext.current


    var firstLaunch by remember {
        mutableStateOf(true)
    }
    val allUsers = viewModel.listOfUsers.collectAsState(emptyList()).value

    var selectedUser by remember {
        mutableStateOf(UserModel(-1, ""))
    }

    if(selectedUser.id != -1) {
        viewModel.subscribeToUserBusiness(selectedUser)
    }

    if (firstLaunch && allUsers.isNotEmpty()) {
        firstLaunch = false
        selectedUser = allUsers[0]
    }

    var isFree = viewModel.isCurrentUserFree.collectAsState().value

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
                onValueChange = {
                    isExpanded = !isExpanded
                },
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
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

        if (selectedUser.id != -1) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f, true)
                    .padding(4.dp, 8.dp, 4.dp, 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Row(
                        if (isFree)
                            Modifier.background(Color(0xFFC5E1A5))
                        else
                            Modifier.background(Color(0xFFEF9A9A))
                    ) {
                        Text(
                            text = if (viewModel.isCurrentUserFree.collectAsState().value)
                                "Свобода!"
                            else
                                "Работа",
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingValues(0.dp, 32.dp, 0.dp, 32.dp)),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp
                        )
                    }

                    Text(
                        text = "График занятости сегодня",
                        Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(0.dp, 16.dp, 0.dp, 4.dp)),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )

                    val currentSchedule = viewModel.currentSchedule.collectAsState().value

                    if (currentSchedule.isEmpty()) {
                        Row {
                            Text(
                                text = "Сегодня выходной!", Modifier
                                    .fillMaxWidth()
                                    .padding(PaddingValues(0.dp, 4.dp, 0.dp, 4.dp)),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {

                        for (entry in currentSchedule) {

                            Text(
                                text = "${entry.startTime} - ${entry.endTime}", Modifier
                                    .fillMaxWidth()
                                    .padding(PaddingValues(0.dp, 4.dp, 0.dp, 4.dp)),
                                textAlign = TextAlign.Center
                            )

                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = {
                            val intent = Intent(context, FullScheduleActivity::class.java)
                            intent.putExtra("user_id", selectedUser.id)
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = modifier
                            .align(Alignment.End)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Полное расписание")
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f, true)
                    .padding(4.dp, 8.dp, 4.dp, 8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    trackColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        Button(
            onClick = {
                val intent = Intent(context, TimeEditActivity::class.java)
                intent.putExtra("user_id", selectedUser.id)
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .alpha(if (selectedUser.id != -1) 1f else 0f)
                .padding(4.dp)
                .height(40.dp)
        ) {
            Text(text = "Изменить расписание")
        }


    }


}


package com.funnco.scheduler.presentation.main

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.domain.DateUtils
import com.funnco.scheduler.domain.model.BusynessType
import com.funnco.scheduler.presentation.composables.TeacherScheduleEntry
import com.funnco.scheduler.presentation.fullSchedule.FullScheduleActivity
import com.funnco.scheduler.presentation.theme.SchedulerTheme
import com.funnco.scheduler.presentation.timeEdit.TimeEditActivity
import java.time.LocalDate


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if(!isRoleHeldByApp(RoleManager.ROLE_CALL_REDIRECTION)){
//            acquireRole(RoleManager.ROLE_CALL_REDIRECTION)
//        }
//        requestOverlayPermission()
        setContent {
            SchedulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(this)
                }
            }
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            return
        }
        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        myIntent.data = Uri.parse("package:$packageName")
        startActivityForResult(myIntent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 101) {
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun isRoleHeldByApp(roleName: String): Boolean {
        val roleManager: RoleManager? = getSystemService(RoleManager::class.java)
        return roleManager!!.isRoleHeld(roleName)
    }

    private fun isRoleAvailable(roleName: String): Boolean {
        val roleManager: RoleManager? = getSystemService(RoleManager::class.java)
        return roleManager!!.isRoleAvailable(roleName)
    }

    private fun acquireRole(roleName: String) {
        val roleManager: RoleManager?
        if (isRoleAvailable(roleName)) {
            roleManager = getSystemService(RoleManager::class.java)
            val intent = roleManager.createRequestRoleIntent(roleName)
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(
                this,
                "Redirection call with role in not available",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Greeting(
    activity: Activity,
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

    var isTeacherModeEnabled by remember {
        mutableStateOf(getTeacherModeFromSharedPrefs(activity))
    }

    if (selectedUser.id != -1) {
        viewModel.getUserBusyness(selectedUser)
    }

    if (firstLaunch && allUsers.isNotEmpty()) {
        firstLaunch = false
        val desiredIdToOpen = getLastSelectedUserFromSharedPrefs(activity)
        selectedUser = allUsers.find { it.id == desiredIdToOpen} ?: allUsers[0]
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
                            writeLastSelectedUserToSharedPrefs(activity, it.id)
                        }
                    )
                }
            }
        }

        val pagerState = rememberPagerState(initialPage = 2) {
            5
        }

        if (selectedUser.id != -1) {
            HorizontalPager(
                state = pagerState,

                modifier = Modifier.weight(12f, true),
                pageSpacing = 4.dp,
                beyondBoundsPageCount = 4
            ) { index ->

                val dayOffset = index - 2

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

                        if (dayOffset == 0) {
                            Row(
                                when (isFree) {
                                    BusynessType.FREE -> Modifier.background(Color(0xFFAED581))
                                    BusynessType.BUSY -> Modifier.background(Color(0xFFE57373))
                                    else -> Modifier.background(Color(0xFFFFF176))
                                }

                            ) {
                                Column {
                                    Text(
                                        text = "Сейчас",
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(PaddingValues(0.dp, 8.dp, 0.dp, 0.dp)),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = when (viewModel.isCurrentUserFree.collectAsState().value) {
                                            BusynessType.FREE -> "Свобода"
                                            BusynessType.BUSY -> "Работа"
                                            BusynessType.PREPARATION -> "Подготовка"
                                            else -> "Завершение"
                                        },
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(PaddingValues(0.dp, 14.dp, 0.dp, 32.dp)),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp
                                    )
                                }

                            }
                        } else {
                            Row(
                                Modifier.background(Color(0x80646464))
                            ) {
                                Column {
                                    Text(
                                        text = DateUtils.mappedDayOffset[dayOffset]!!,
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(PaddingValues(0.dp, 32.dp, 0.dp, 32.dp)),
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Расписание " + if (dayOffset == 0) "сегодня" else "",
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingValues(0.dp, 16.dp, 0.dp, 4.dp)),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )

                        var currentSchedule = if (dayOffset == 0) {
                            viewModel.currentSchedule.collectAsState().value
                        } else {
                            viewModel.getScheduleForDay(selectedUser, dayOffset)
                        }

                        if (currentSchedule.isEmpty()) {
                            Row {
                                Text(
                                    text = "Выходной!", Modifier
                                        .fillMaxWidth()
                                        .padding(PaddingValues(0.dp, 4.dp, 0.dp, 4.dp)),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            if (!isTeacherModeEnabled) {
                                for (entry in currentSchedule) {
                                    Text(
                                        text = "${entry.startTime} - ${entry.endTime}", Modifier
                                            .fillMaxWidth()
                                            .padding(PaddingValues(0.dp, 4.dp, 0.dp, 4.dp)),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {

                                var weekOffset : Long = 0
                                var currentDayNumber = LocalDate.now().dayOfWeek.value
                                if (currentDayNumber + dayOffset > 7) {
                                    weekOffset = 1
                                }
                                if(currentDayNumber + dayOffset < 0) {
                                    weekOffset = -1
                                }

                                for (entry in currentSchedule) {
                                    TeacherScheduleEntry(
                                        entry = entry,
                                        weekOffset,
                                        onTempNoteEditingStateChange = {
                                            viewModel.isUpdateAllowed = !it
                                        })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(5.dp)
                                .fillMaxWidth()
                                .padding(8.dp, 4.dp, 8.dp, 0.dp)
                                .background(
                                    Color.White
                                )
                                .alpha(0.75f),
                            content = {}
                        )

                        Row {
                            Text(
                                modifier = Modifier
                                    .weight(5f, true)
                                    .align(Alignment.CenterVertically)
                                    .padding(12.dp, 0.dp, 0.dp, 0.dp),
                                text = "Режим учителя",
                                textAlign = TextAlign.Start,
                            )
                            Switch(modifier = Modifier
                                .scale(0.85f)
                                .padding(0.dp, 0.dp, 12.dp, 0.dp),
                                checked = isTeacherModeEnabled,
                                onCheckedChange = {
                                    isTeacherModeEnabled = !isTeacherModeEnabled
                                    writeTeacherModeToSharedPrefs(activity, isTeacherModeEnabled)
                                })
                        }


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

        if (isTeacherModeEnabled) {
            Button(
                onClick = {
                    val intent = Intent(context, TimeEditActivity::class.java)
                    intent.putExtra("user_id", selectedUser.id)
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = modifier
                    .weight(1f, false)
                    .align(Alignment.End)
                    .fillMaxWidth()
                    .alpha(if (selectedUser.id != -1) 1f else 0f)
                    .padding(4.dp)
            ) {
                Text(text = "Изменить расписание")
            }
        }

        TextButton(
            onClick = {
                val intent = Intent(context, FullScheduleActivity::class.java)
                intent.putExtra("user_id", selectedUser.id)
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .weight(1f, false)
                .align(Alignment.End)
                .padding(4.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Полное расписание")
        }
    }
}


fun writeTeacherModeToSharedPrefs(activity: Activity, mode: Boolean) {
    val sharedPrefs = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean("TeacherMode", mode).commit()
}

fun getTeacherModeFromSharedPrefs(activity: Activity): Boolean {
    val sharedPrefs = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("TeacherMode", false)
}

fun writeLastSelectedUserToSharedPrefs(activity: Activity, userId: Int){
    val sharedPrefs = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    sharedPrefs.edit().putInt("LastUserId", userId).commit()
}

fun getLastSelectedUserFromSharedPrefs(activity: Activity): Int{
    val sharedPrefs = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    return sharedPrefs.getInt("LastUserId", 1)
}


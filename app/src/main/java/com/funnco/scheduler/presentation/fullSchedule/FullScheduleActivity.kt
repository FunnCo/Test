package com.funnco.scheduler.presentation.fullSchedule

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.domain.DateUtils
import com.funnco.scheduler.presentation.composables.TeacherScheduleEntry
import com.funnco.scheduler.presentation.composables.WeekSwitch
import com.funnco.scheduler.presentation.theme.SchedulerTheme

class FullScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchedulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting3(intent.getIntExtra("user_id", -1), this)
                }
            }
        }
    }
}

@Composable
fun Greeting3(userId: Int, activity: Activity) {
    var viewModel: FullScheduleActivityViewModel = viewModel()
    val selectedUser = viewModel.getCurrentUser(userId)

    var isFirstLaunch by remember {
        mutableStateOf(true)
    }
    if (isFirstLaunch) {
        viewModel.getUserSchedules(selectedUser)
        isFirstLaunch = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
    ) {

        Text(
            text = "Полное расписание\nпользователя ${selectedUser.nickName}",
            Modifier
                .fillMaxWidth()
                .padding(PaddingValues(0.dp, 8.dp, 0.dp, 4.dp)),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        var weekOffset by remember {
            mutableIntStateOf(0)
        }

        WeekSwitch(startingOffset = weekOffset,onWeekChange = {newOffset -> weekOffset = newOffset })
        Log.i("TAG", "current weekoffset is ${weekOffset}")
        LazyColumn(
            content = {
                items(7) { index ->
                    TimeScheduleCard(
                        schedule = viewModel.userSchedules.collectAsState().value.filter { it.dayNumber == index + 1 },
                        index + 1,
                        activity,
                        weekOffset
                    )

                }

            })

    }

}

@Composable
fun TimeScheduleCard(schedule: List<ScheduleModel>, dayNumber: Int, activity: Activity, weekOffset: Int) {
    var viewModel: FullScheduleActivityViewModel = viewModel()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 4.dp, 4.dp, 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "${DateUtils.mappedDays[dayNumber]}",
                Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(0.dp, 8.dp, 0.dp, 4.dp)),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
            if (schedule.isEmpty()) {
                Row {
                    Text(
                        text = "Свободый день", Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(0.dp, 4.dp, 0.dp, 4.dp)),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                if (getTeacherModeFromSharedPrefs(activity = activity)) {
                    for (entry in schedule) {
                        TeacherScheduleEntry(entry = entry, weekOffset.toLong())
                    }
                } else {
                    for (entry in schedule) {
                        Text(
                            text = "${entry.startTime} - ${entry.endTime}", Modifier
                                .fillMaxWidth()
                                .padding(PaddingValues(0.dp, 4.dp, 0.dp, 4.dp)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp))
        }


    }
}



fun getTeacherModeFromSharedPrefs(activity: Activity): Boolean {
    val sharedPrefs = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("TeacherMode", false)
}

package com.funnco.scheduler.presentation.fullSchedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.domain.DateUtils
import com.funnco.scheduler.presentation.composables.TeacherScheduleEntry
import com.funnco.scheduler.presentation.composables.WeekSwitch
import com.funnco.scheduler.presentation.main.MainActivity
import com.funnco.scheduler.presentation.theme.SchedulerTheme
import java.time.format.DateTimeFormatter

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
                    Greeting3(intent.getStringExtra("user_id") ?: "", this)
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }
}

@Composable
fun Greeting3(userId: String, activity: Activity) {
    var viewModel: FullScheduleActivityViewModel = viewModel()
    val selectedUser = viewModel.getCurrentUser(userId)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
    ) {

        Text(
            text = "Полное расписание\nпользователя ${selectedUser.name}",
            Modifier
                .fillMaxWidth()
                .padding(PaddingValues(0.dp, 8.dp, 0.dp, 4.dp)),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        var weekOffset by remember {
            mutableIntStateOf(0)
        }

        WeekSwitch(
            startingOffset = weekOffset,
            onWeekChange = { newOffset ->
                weekOffset = newOffset
            }
        )

        Log.i("TAG", "current weekoffset is ${weekOffset}")

        val weeksSchedule = viewModel.weeksSchedule[weekOffset]?.collectAsState()?.value ?: emptyList()
        val daysSchedules = viewModel.parseWeekScheduleToMap(weeksSchedule)

        LazyColumn {
            for (i in 1 .. 7){
                item {
                    TimeScheduleCard(
                        schedule = daysSchedules[i] ?: emptyList(),
                        dayNumber = i,
                        activity = activity
                    )
                }
            }
        }
    }
}

@Composable
fun TimeScheduleCard(
    schedule: List<EventModel>,
    dayNumber: Int,
    activity: Activity,
) {

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

            Log.d("TestBug", "day number is ${dayNumber}")

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
                if (getDetailedModeFromSharedPrefs(activity = activity)) {
                    for (entry in schedule) {
                        TeacherScheduleEntry(entry = entry)
                    }
                } else {
                    for (entry in schedule) {
                        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                        Text(
                            text = "${entry.startTime?.plusHours(3)?.format(timeFormatter)} - ${entry.endTime?.plusHours(3)?.format(timeFormatter)}", Modifier
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


fun getDetailedModeFromSharedPrefs(activity: Activity): Boolean {
    val sharedPrefs = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("DetailedMode", false)
}

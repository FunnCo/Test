package com.funnco.scheduler.presentation.timeEdit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnco.scheduler.data.repository.model.TemplateEventModel
import com.funnco.scheduler.domain.DateUtils
import com.funnco.scheduler.domain.model.WeekDay
import com.funnco.scheduler.presentation.main.MainActivity
import com.funnco.scheduler.presentation.theme.SchedulerTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TimeEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchedulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2(intent.getStringExtra("user_id")!!, this)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting2(userId: String, activity: Activity) {

    var viewModel: TimeEditActivityViewModel = viewModel()
    val selectedUser = viewModel.getCurrentUser(userId)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
    ) {

        Text(
            text = "Редактирование графика занятости\nпользователя ${selectedUser.name}",
            Modifier
                .fillMaxWidth()
                .padding(PaddingValues(0.dp, 8.dp, 0.dp, 4.dp)),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )


        Text(
            text = "Нажмите на запись, которую хотите изменить, и выберите нужное время",
            Modifier
                .fillMaxWidth()
                .padding(PaddingValues(0.dp, 16.dp, 0.dp, 4.dp)),
            textAlign = TextAlign.Center,
        )

        var currentSelectedDay by remember {
            mutableIntStateOf(LocalDate.now().dayOfWeek.value)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 8.dp, 4.dp, 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                for (i in 1..7)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(2.dp, 0.dp),
                        colors = if (currentSelectedDay == i) CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) else CardDefaults.cardColors(),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            currentSelectedDay = i
                        }
                    ) {
                        Text(
                            text = "${DateUtils.mappedShortDays[i]}",
                            modifier = Modifier
                                .padding(0.dp, 8.dp)
                                .align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                        )
                    }
            }
        }



        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f, true)
                .padding(4.dp, 8.dp, 4.dp, 8.dp)
                .alpha(if (selectedUser.id != "") 1f else 0f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                val currentSchedule =
                    viewModel.daysTemplates[currentSelectedDay]?.collectAsState()?.value
                        ?: emptyList()

                if (currentSchedule.isEmpty()) {
                    Row {
                        Text(
                            text = "Сегодня выходной!", Modifier
                                .fillMaxWidth()
                                .padding(PaddingValues(12.dp, 4.dp, 12.dp, 4.dp)),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Начало",
                            textAlign = TextAlign.Center, modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        Text(
                            text = "", modifier = Modifier
                                .padding(4.dp)
                        )
                        Text(
                            text = "Конец", modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .weight(1f),
                            textAlign = TextAlign.Center
                        )

                    }
                    for (entry in currentSchedule) {
                        TimeScheduleCard(template = entry)
                    }
                }

                var isNewTemplateBeingCreated by remember {
                    mutableStateOf(false)
                }
                OutlinedCard(
                    modifier = Modifier.padding(12.dp, 2.dp),
                    border = BorderStroke(width = 0.dp, color = Color.Transparent),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                    ),
                    onClick = {
                        isNewTemplateBeingCreated = true
                    }
                ) {

                    if(isNewTemplateBeingCreated){
                        val newTemplateEventModel = TemplateEventModel()
                        newTemplateEventModel.day = WeekDay.values().findLast { entry -> entry.value == currentSelectedDay }!!.name
                        TimeScheduleCard(newTemplateEventModel, true) {
                            isNewTemplateBeingCreated = false
                        }
                    } else {
                        Image(
                            Icons.Filled.Add,
                            contentDescription = "Add new entry",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .align(
                                    Alignment.CenterHorizontally
                                )
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }

                }
            }
        }

        TextButton(
            onClick = { activity.onBackPressed() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .padding(4.dp)
                .height(40.dp)
        ) {
            Text(text = "Назад")
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScheduleCard(template: TemplateEventModel, isNewTemplate: Boolean = false, callback: (wasCreated: Boolean) -> Unit = {}) {

    var viewModel: TimeEditActivityViewModel = viewModel()

    var initHour by remember {
        mutableIntStateOf(0)
    }
    var initMinutes by remember {
        mutableIntStateOf(0)
    }
    var isStartTimeEdited by remember {
        mutableStateOf(true)
    }
    var isDialogNeededNow by remember {
        mutableStateOf(isNewTemplate)
    }

    OutlinedCard(
        modifier = Modifier.padding(12.dp, 2.dp),
        border = BorderStroke(width = 0.dp, color = Color.Transparent),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        )
    ) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
                .weight(1f)
                .height(40.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    initHour = template.startTime?.hour ?: 0
                    initMinutes = template.startTime?.minute ?: 0
                    isDialogNeededNow = true
                    isStartTimeEdited = true
                }) {
                Text(
                    text = "${template.startTime?.plusHours(3)?.format(timeFormatter)}",
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "|", modifier = Modifier
                    .padding(4.dp)
                    .alpha(0.5f)
                    .align(Alignment.CenterVertically), fontSize = 18.sp
            )
            TextButton(modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
                .height(40.dp)
                .weight(1f),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    initHour = template.startTime?.hour ?: 0
                    initMinutes = template.startTime?.minute ?: 0
                    isDialogNeededNow = true
                    isStartTimeEdited = false

                }) {
                Text(
                    text = "${template.endTime?.plusHours(3)?.format(timeFormatter)}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (isDialogNeededNow) {
        var timePickerState = rememberTimePickerState(
            is24Hour = true,
            initialHour = initHour,
            initialMinute = initMinutes
        )

        var noteText by remember {
            mutableStateOf(template.description)
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        AlertDialog(
            onDismissRequest = {
                isDialogNeededNow = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val currentOffset = ZonedDateTime.now().offset
                        if (isStartTimeEdited) {
                            template.startTime =
                                OffsetTime.of(LocalTime.of(timePickerState.hour, timePickerState.minute), currentOffset)
                        } else {
                            template.endTime =
                                OffsetTime.of(LocalTime.of(timePickerState.hour, timePickerState.minute), currentOffset)
                        }
                        if(template.endTime == null){
                            template.endTime = template.startTime!!.plusHours(1)
                        }
                        if(template.startTime == null){
                            template.startTime = template.endTime!!.minusHours(1)
                        }
                        if(template.description == null){
                            template.description = ""
                        }
                        isLoading = true
                        viewModel.createOrUpdateTemplate(template) {
                            isLoading = false
                            callback(true)
                            isDialogNeededNow = false
                        }
                    }
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isDialogNeededNow = false
                        callback(false)
                    }
                ) {
                    Text("Отмена")
                }
            },
            text = {
                Column {
                    TimePicker(state = timePickerState)
                    TextField(
                        label = { Text(text = "Описание") },
                        value = noteText ?: "", onValueChange = {
                            noteText = it
                            template.description = it
                        }
                    )
                    if(template.id != null){
                        TextButton(onClick = {
                            viewModel.deleteTemplate(template.id!!) {
                                isDialogNeededNow = false
                                isLoading = false
                            }
                        }) {
                            Text(text = "Удалить запись", color = Color(239, 83, 80, 255))
                        }
                    }
                }
            }
        )
    }
}



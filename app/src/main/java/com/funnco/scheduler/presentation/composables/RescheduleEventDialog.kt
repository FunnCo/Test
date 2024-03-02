package com.funnco.scheduler.presentation.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnco.scheduler.data.model.ScheduleModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleDialog(scheduleEntry: ScheduleModel, onDismissRequest: () -> Unit, onDateSelected: (date: LocalDate, time: LocalTime) -> Unit) {
    var time by remember {
        mutableStateOf("00:00")
    }
    var date by remember {
        mutableStateOf("21.02")
    }

    var isTimePickerDialogShown by remember {
        mutableStateOf(false)
    }

    var isDatePickerDialogShown by remember {
        mutableStateOf(false)
    }

    var timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = scheduleEntry.startTime.hour,
        initialMinute = scheduleEntry.startTime.minute
    )

    var datePickerState = rememberDatePickerState(System.currentTimeMillis())

    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate(),
                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                    )
                }
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Отмена")
            }
        },
        title = { Text("Дата и время переноса") },
        text = {

            Row {
                TextField(
                    value = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate().format(
                        DateTimeFormatter.ofPattern("E, dd.MM")),
                    onValueChange = {
                        date = it
                    },
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 0.dp, 4.dp, 0.dp)
                        .clickable {
                            isDatePickerDialogShown = true
                        },
                    colors = TextFieldDefaults.textFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                TextField(
                    value = LocalTime.of(timePickerState.hour, timePickerState.minute).toString(),
                    onValueChange = {
                        time = it
                    },
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp, 0.dp, 0.dp, 0.dp)
                        .clickable { isTimePickerDialogShown = true },
                    colors = TextFieldDefaults.textFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

            }

            // TimePicker
            if (isTimePickerDialogShown) {
                AlertDialog(
                    onDismissRequest = {
                        isTimePickerDialogShown = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                isTimePickerDialogShown = false
                            }
                        ) {
                            Text("Подтвердить")
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }

            // DatePicker
            if (isDatePickerDialogShown) {
                DatePickerDialog(
                    onDismissRequest = {
                        isDatePickerDialogShown = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                isDatePickerDialogShown = false
                            }
                        ) {
                            Text("Подтвердить")
                        }
                    },
                    content = {
                        DatePicker(state = datePickerState)
                    }
                )
            }
        }
    )
}
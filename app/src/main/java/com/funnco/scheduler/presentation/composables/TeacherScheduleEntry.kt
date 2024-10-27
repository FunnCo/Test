package com.funnco.scheduler.presentation.composables

import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnco.scheduler.data.repository.ApiService
import com.funnco.scheduler.data.repository.model.EventModel
import com.funnco.scheduler.data.repository.model.NoteModel
import com.funnco.scheduler.domain.interactor.DataInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScheduleEntry(
    entry: EventModel,
    onTempNoteEditingStateChange: (isEditing: Boolean) -> Unit = {}
) {

    var notes = MutableStateFlow(entry.notes)

    Row(modifier = Modifier.height(IntrinsicSize.Max)) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        Text(
            text = "${entry.startTime?.format(timeFormatter)} - ${entry.endTime?.format(timeFormatter)}", Modifier
                .padding(PaddingValues(12.dp, 4.dp, 12.dp, 4.dp)),
            textAlign = TextAlign.Start
        )
        Column {
            Row {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .alpha(0.35f)
                        .padding(0.dp, 4.dp, 0.dp, 4.dp),
                    content = {},
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                )
                Text(
                    text = entry.description ?: "", Modifier
                        .weight(2f)
                        .padding(PaddingValues(8.dp, 4.dp, 12.dp, 4.dp)),
                    textAlign = TextAlign.Start
                )

                val interactionSource = remember {
                    MutableInteractionSource()
                }

                var isMenuExpanded by remember {
                    mutableStateOf(false)
                }

                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Box {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(4.dp, 4.dp, 8.dp, 4.dp)
                                .clickable(
                                    enabled = true,
                                    interactionSource = interactionSource,
                                    indication = rememberRipple(bounded = false, radius = 16.dp)
                                ) {
                                    isMenuExpanded = true
                                }
                        )
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }) {
                            TextButton(modifier = Modifier.padding(8.dp, 4.dp), onClick = {
                                isMenuExpanded = false

                                val tempList =
                                    notes.value?.toMutableList() ?: mutableListOf()
                                tempList.add(
                                    NoteModel(
                                        null,
                                        "",
                                        false
                                    )
                                )
                                notes.value = tempList
                                GlobalScope.launch {
                                    onTempNoteEditingStateChange(true)
                                    delay(5000L)
                                    if (!isNoteBeingEdited) {
                                        onTempNoteEditingStateChange(false)
                                    }
                                }
                                entry.notes = notes.value

                            }) {
                                Text("Новая заметка")
                            }

                            TextButton(modifier = Modifier.padding(8.dp, 4.dp), onClick = {
                                isMenuExpanded = false

                                ApiService.deleteSingleEvent(entry.id!!){
                                    // Короче временно тут нихрена, вообще должен обновиться интерфейс.
                                }

                            }) {
                                Text("Отмена")
                            }

                            var isRescheduleDialogNeeded by remember {
                                mutableStateOf(false)
                            }

                            TextButton(
                                modifier = Modifier.padding(8.dp, 4.dp),
                                onClick = {
                                    isRescheduleDialogNeeded = true
                                }) {
                                Text("Перенос")
                            }

                            if (isRescheduleDialogNeeded) {
                                RescheduleDialog(
                                    entry,
                                    onDismissRequest = {
                                        isRescheduleDialogNeeded = false
                                        isMenuExpanded = false
                                    },
                                    onDateSelected = { date, time ->
                                        isRescheduleDialogNeeded = false
                                        isMenuExpanded = false

                                        val eventTimeLength = Duration.between(entry.startTime, entry.endTime)
                                        val currentOffset = ZonedDateTime.now().offset

                                        entry.date = date
                                        entry.startTime = OffsetTime.of(time, currentOffset)
                                        entry.endTime = entry.startTime!!.plus(eventTimeLength)
                                        DataInteractor.pushEvent(entry){

                                        }

//                                        val newNote = NoteModel(
//                                            null,
//                                            "Перенос на ${date.format(DateTimeFormatter.ofPattern("E, dd.MM"))} $time",
//                                            LocalDate
//                                                .now()
//                                                .minusDays(LocalDate.now().dayOfWeek.value.toLong())
//                                                .plusDays(entry.dayNumber.toLong())
//                                                .plusWeeks(offsetWeeks),
//                                            entry.id!!,
//                                            false
//                                        )
//
//                                        tempList.add(newNote)
//
//                                        DataInteractor.insertOrChangeTempNote(tempList.last()) { isSuccess, newNote ->
//                                            if (isSuccess) {
//                                                tempList.last().id = newNote.id
//                                            }
//                                        }

//                                        notes.value = tempList
//                                        entry.tempNotes = notes.value
//                                        GlobalScope.launch {
//                                            onTempNoteEditingStateChange(true)
//                                            delay(5000L)
//                                            if (!isNoteBeingEdited) {
//                                                onTempNoteEditingStateChange(false)
//                                            }
//                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            for (note in notes.collectAsState().value!!) {
                Note(note = note, eventId = entry.id!!, onTempNoteEditingStateChange)
            }
        }
    }
}

var isNoteBeingEdited = false
var scheduledJob: Job? = null
fun handleUserInput(codeToSchedule: () -> Unit, onStateChange: (Boolean) -> Unit) {
    onStateChange(true)
    isNoteBeingEdited = true
    if (scheduledJob != null) {
        scheduledJob!!.cancel()
    }
    scheduledJob = GlobalScope.launch {
        val expectedTimeToFire = LocalTime.now().plusSeconds(2)
        while (isActive) {
            if (LocalTime.now().second == expectedTimeToFire.second) {
                codeToSchedule()
                onStateChange(false)
                isNoteBeingEdited = false
                break
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Note(
    note: NoteModel,
    eventId: String,
    onTempNoteEditingStateChange: (Boolean) -> Unit
){
    var currentValue by remember {
        mutableStateOf(note.content!!)
    }
    var isFlagged by remember {
        mutableStateOf(note.flag ?: false)
    }

    Card(
        modifier = Modifier
            .padding(0.dp, 0.dp, 12.dp, 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row {
            BasicTextField(
                value = currentValue,
                onValueChange = {
                    currentValue = it
                    note.content = currentValue
                    handleUserInput({
                        DataInteractor.pushNote(note, eventId) {
                            // WTF? ПОФИГ должно и так работать
                        }
                    }, onTempNoteEditingStateChange)
                },
                modifier = Modifier
                    .weight(2f)
                    .padding(8.dp, 4.dp, 12.dp, 4.dp),
                textStyle = TextStyle.Default.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
            )
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                Checkbox(
                    checked = isFlagged,
                    onCheckedChange = {
                        isFlagged = !isFlagged
                        note.flag = isFlagged
                        handleUserInput(
                            {
                                DataInteractor.pushNote(note, eventId) {
                                    // WTF? ПОФИГ должно и так работать
                                }
                            }, onTempNoteEditingStateChange
                        )
                    },
                    modifier = Modifier
                        .scale(0.85f)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}
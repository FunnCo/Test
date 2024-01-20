package com.funnco.scheduler.presentation.composables

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.funnco.scheduler.data.model.ScheduleModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScheduleEntry(entry: ScheduleModel) {

    var isPayed by remember {
        mutableStateOf(false)
    }

    Row(modifier = Modifier.height(IntrinsicSize.Max)) {
        Text(
            text = "${entry.startTime} - ${entry.endTime}", Modifier
                .padding(PaddingValues(12.dp, 4.dp, 12.dp, 4.dp)),
            textAlign = TextAlign.Start
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .alpha(0.35f)
                .padding(0.dp, 4.dp, 0.dp, 4.dp),
            content = {},
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        )
        Text(
            text = "${entry.note ?: ""}", Modifier
                .weight(2f)
                .padding(PaddingValues(8.dp, 4.dp, 12.dp, 4.dp)),
            textAlign = TextAlign.Start
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(
                checked = isPayed,
                onCheckedChange = { isPayed = !isPayed },
                modifier = Modifier
                    .scale(0.85f)
                    .padding(4.dp, 4.dp,12.dp, 8.dp)
            )
        }
    }
}
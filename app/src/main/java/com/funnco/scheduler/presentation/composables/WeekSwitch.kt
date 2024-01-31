package com.funnco.scheduler.presentation.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.funnco.scheduler.domain.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekSwitch(startingOffset: Int = 0,onWeekChange: (weekOffset: Int) -> Unit) {
    var currentOffset by remember {
        mutableIntStateOf(startingOffset)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 4.dp, 4.dp, 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(onClick = {
                    if(currentOffset != -4) {
                        currentOffset--
                        onWeekChange(currentOffset)
                    }
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = DateUtils.mappedWeekOffset[currentOffset]!!,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically).weight(2f)
            )

            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(onClick = {
                    if(currentOffset != 4) {
                        currentOffset++
                        onWeekChange(currentOffset)
                    }
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                }
            }
        }
    }
}
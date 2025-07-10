package com.base.features.portfolio.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.features.portfolio.presentation.TimeRange
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

enum class ChartTimeRange(val label: String) {
    DAY("24H"),
    WEEK("1W"),
    MONTH("1M"),
    YEAR("1Y"),
    ALL("ALL")
}

@Composable
fun PortfolioChart(
    modifier: Modifier = Modifier,
    selectedRange: ChartTimeRange,
    onRangeSelected: (ChartTimeRange) -> Unit,
    chartData: List<Pair<Long, Double>>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Time range selector
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            ChartTimeRange.entries.forEachIndexed { index, range ->
                SegmentedButton(
                    selected = range == selectedRange,
                    onClick = { onRangeSelected(range) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = TimeRange.entries.size
                    )
                ) {
                    Text(range.label)
                }
            }
        }

        // Chart
        val chartEntryModel = remember(chartData) {
            ChartEntryModelProducer(
                chartData.mapIndexed { index, (_, value) ->
                    entryOf(index.toFloat(), value.toFloat())
                }
            ).getModel()
        }

        ProvideChartStyle {
            Chart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(top = 16.dp),
                chart = lineChart(),
                model = chartEntryModel as ChartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis()
            )
        }
    }
} 
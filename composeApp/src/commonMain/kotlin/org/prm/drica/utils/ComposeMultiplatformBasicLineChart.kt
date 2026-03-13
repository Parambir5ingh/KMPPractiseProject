package org.prm.drica.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
//import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
//import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
//import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
//import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
//import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
//import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
//import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart

@Composable
fun ComposeMultiplatformBasicLineChart(modifier: Modifier = Modifier) {
  val modelProducer = remember { CartesianChartModelProducer() }
  LaunchedEffect(Unit) {
    modelProducer.runTransaction {
      // Learn more: https://patrykandpatrick.com/z5ah6v.
      lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
    }
  }
  CartesianChartHost(
    chart =
      rememberCartesianChart(
        rememberLineCartesianLayer(),
        startAxis = VerticalAxis.rememberStart(),
        bottomAxis = HorizontalAxis.rememberBottom(),
      ),
    modelProducer = modelProducer,
    modifier = modifier,
  )
}
package com.lksnext.benchmark

import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test

class MainActivityBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun parkingGrid() = benchmarkRule.measureRepeated(
        packageName = "com.lksnext.parkingagarcia",
        metrics = listOf(FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None(),
        setupBlock = {
            val intent = Intent().apply {
                component = ComponentName("com.lksnext.parkingagarcia", "com.lksnext.parkingagarcia.view.activity.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivityAndWait(intent)
        }
    ) {
        val grid = device.findObject(By.res(packageName, "svParking"))
        grid.setGestureMargin(device.displayWidth / 3)
        grid.fling(Direction.DOWN)
        grid.fling(Direction.UP)
        grid.fling(Direction.RIGHT)
        grid.fling(Direction.LEFT)
    }

    @Test
    fun mainFragmentStartUp() = benchmarkRule.measureRepeated(
        packageName = "com.lksnext.parkingagarcia",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None(),
    ) {
        pressHome()
        val intent = Intent().apply {
            component = ComponentName("com.lksnext.parkingagarcia", "com.lksnext.parkingagarcia.view.activity.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityAndWait(intent)
    }

    @Test
    fun changeFragment() = benchmarkRule.measureRepeated(
        packageName = "com.lksnext.parkingagarcia",
        metrics = listOf(FrameTimingMetric(), StartupTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None(),
        setupBlock = {
            val intent = Intent().apply {
                component = ComponentName("com.lksnext.parkingagarcia", "com.lksnext.parkingagarcia.view.activity.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivityAndWait(intent)
        }
    ) {
        repeat(3) {
            val reservationsBtn = device.findObject(By.res(packageName, "reservations"))
            reservationsBtn.click()
            val newResBtn = device.findObject(By.res(packageName, "newres"))
            newResBtn.click()
        }
    }
}
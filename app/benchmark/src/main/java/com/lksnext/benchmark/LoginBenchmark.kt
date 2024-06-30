package com.lksnext.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun login() = benchmarkRule.measureRepeated(
        packageName = "com.lksnext.parkingagarcia",
        metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.DEFAULT,
    ) {
        startActivityAndWait()
        device.findObject(By.res(packageName, "emailText")).text = "benchmark@benchmark.com"
        device.findObject(By.res(packageName, "passwordText")).text = "asdfasdf"
        device.findObject(By.res(packageName, "loginButton")).click()
    }
}

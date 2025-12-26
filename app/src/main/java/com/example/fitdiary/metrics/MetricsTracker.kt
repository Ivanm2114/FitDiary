package com.example.fitdiary.metrics

import android.os.SystemClock
import java.util.concurrent.atomic.AtomicLong

data class MetricsSnapshot(
    val crashRate: Double,
    val startTime: Double,
    val retention: Double,
    val sessionLength: Double,
    val errorRate: Double,
    val totalRequests: Long,
    val errorRequests: Long,
)

object MetricsTracker {
    private val processStartMs = AtomicLong(0L)
    private val appStartTimeMs = AtomicLong(0L)
    private val sessionStartMs = AtomicLong(0L)
    private val sessionAccumMs = AtomicLong(0L)
    private val totalRequests = AtomicLong(0L)
    private val errorRequests = AtomicLong(0L)

    fun markProcessStart() {
        if (processStartMs.get() == 0L) {
            processStartMs.set(SystemClock.elapsedRealtime())
        }
    }

    fun markAppStart() {
        if (appStartTimeMs.get() != 0L) {
            return
        }
        val processStart = processStartMs.get()
        if (processStart == 0L) {
            return
        }
        appStartTimeMs.set(SystemClock.elapsedRealtime() - processStart)
    }

    fun onSessionStart() {
        if (sessionStartMs.get() == 0L) {
            sessionStartMs.set(SystemClock.elapsedRealtime())
        }
    }

    fun onSessionStop() {
        val start = sessionStartMs.getAndSet(0L)
        if (start != 0L) {
            sessionAccumMs.addAndGet(SystemClock.elapsedRealtime() - start)
        }
    }

    fun recordRequest(success: Boolean) {
        totalRequests.incrementAndGet()
        if (!success) {
            errorRequests.incrementAndGet()
        }
    }

    fun snapshot(): MetricsSnapshot {
        val appStart = appStartTimeMs.get().toDouble() / 1000.0
        val sessionStart = sessionStartMs.get()
        val sessionMs = if (sessionStart == 0L) {
            sessionAccumMs.get()
        } else {
            sessionAccumMs.get() + (SystemClock.elapsedRealtime() - sessionStart)
        }
        val total = totalRequests.get()
        val errors = errorRequests.get()
        val errorRate = if (total == 0L) 0.0 else errors.toDouble() / total.toDouble()
        return MetricsSnapshot(
            crashRate = 0.0,
            startTime = appStart,
            retention = 0.0,
            sessionLength = sessionMs.toDouble() / 1000.0,
            errorRate = errorRate,
            totalRequests = total,
            errorRequests = errors
        )
    }
}

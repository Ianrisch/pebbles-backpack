package tech.sethi.pebbles.backpack.debounce

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
val futures = ConcurrentHashMap<UUID, Future<*>>()

fun debounce(time: Duration, fn: () -> Unit): () -> Unit {
    val key = UUID.randomUUID()
    return {
        futures[key]?.cancel(false)
        val future = scheduler.schedule({ fn() }, time.inWholeSeconds, TimeUnit.SECONDS)
        futures[key] = future
    }
}
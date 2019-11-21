package com.github.csandiego.timesup.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.roundToLong

class DefaultTimer @Inject constructor(private val currentTimeProvider: CurrentTimeProvider) :
    ManualTimer() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun start() {
        super.start()
        job = coroutineScope.launch {
            val duration = timeLeft.value!! * 1000L
            launch {
                flow {
                    val start = currentTimeProvider.currentTimeMillis()
                    var next = start
                    while (state.value == Timer.State.STARTED &&
                        currentTimeProvider.currentTimeMillis() < start + duration) {
                        emit(start + duration - currentTimeProvider.currentTimeMillis())
                        next += 1000L
                        delay(next - currentTimeProvider.currentTimeMillis())
                    }
                }.collect {
                    advanceBy(timeLeft.value!! - (it / 1000.0).roundToLong())
                }
            }
            launch {
                delay(duration)
                advanceBy(timeLeft.value!!)
            }
        }
    }

    override fun pause() {
        super.pause()
        job!!.cancel()
        job = null
    }

    override fun clear() {
        super.clear()
        job?.cancel()
        job = null
    }
}
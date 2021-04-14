package com.bytepoets.sample.androidtesting.util.event

interface EventHandler<V> {
    fun onEventUnHandled(event: V)
}

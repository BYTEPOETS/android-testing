package com.bytepoets.sample.androidtesting.util.event

import androidx.lifecycle.Observer
import org.jetbrains.annotations.NotNull

class EventObserver<T>(@NotNull private val onEventUnhandledContent: EventHandler<T>) :
    Observer<Event<T>?> {
    override fun onChanged(event: Event<T>?) {
        event?.contentIfNotHandled?.let {
            onEventUnhandledContent.onEventUnHandled(it)
        }
    }

}
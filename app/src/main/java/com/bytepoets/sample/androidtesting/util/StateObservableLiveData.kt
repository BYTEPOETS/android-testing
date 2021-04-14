package com.bytepoets.sample.androidtesting.util

import androidx.lifecycle.MutableLiveData

class StateObservableLiveData<T> : MutableLiveData<T>() {

    var onActiveListener: (() -> Unit)? = null
    var onInactiveListener: (() -> Unit)? = null

    override fun onActive() {
        onActiveListener?.invoke()
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
        onInactiveListener?.invoke()
    }
}

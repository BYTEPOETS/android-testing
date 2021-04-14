package com.bytepoets.sample.androidtesting.util.event

class Event<T>(content: T?) {
    private val mContent: T
    private var hasBeenHandled = false
    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            mContent
        }

    fun peekContent(): T {
        return mContent
    }

    fun hasBeenHandled(): Boolean {
        return hasBeenHandled
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (mContent != other.mContent) return false
        if (hasBeenHandled != other.hasBeenHandled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mContent?.hashCode() ?: 0
        result = 31 * result + hasBeenHandled.hashCode()
        return result
    }

    init {
        requireNotNull(content) { "null values in Event are not allowed." }
        mContent = content
    }
}

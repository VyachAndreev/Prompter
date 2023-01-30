package com.example.prompter.view

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyScrollView(context: Context, attrs: AttributeSet?): NestedScrollView(context, attrs) {
    private val scopeMain by lazy { CoroutineScope(Dispatchers.Main) }
    private var isScrolling = false
    private var previousTime: Long? = null
    private var delta = 0L
    var timeForPixel = 0L

    interface OnScrollChangedListener {
        fun notifyNothingChanged()
    }
    private lateinit var listener: OnScrollChangedListener

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (t - oldt <= 0) {
            listener.notifyNothingChanged()
        }
    }

    private fun scrollText() {
        scopeMain.launch {
            val currentTime = SystemClock.uptimeMillis()
            previousTime?.let {
                delta += currentTime - it
                scrollBy(0, (delta / timeForPixel).toInt())
                delta %= timeForPixel
            }
            if (isScrolling) {
                previousTime = currentTime
                scrollText()
            }
        }
    }

    fun startScrolling() {
        isScrolling = true
        scrollText()
    }

    fun stopScrolling() {
        previousTime = null
        isScrolling = false
    }

    fun getIsScrolling(): Boolean {
        return isScrolling
    }

    fun setListener(listener: OnScrollChangedListener) {
        this.listener = listener
    }
}
package com.example.prompter.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class MyScrollView(context: Context, attrs: AttributeSet?): NestedScrollView(context, attrs) {
    interface OnScrollChangedListener {
        fun notifyNothingChanged()
    }
    private lateinit var listener: OnScrollChangedListener

    fun setListener(listener: OnScrollChangedListener) {
        this.listener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (t - oldt <= 0) {
            listener.notifyNothingChanged()
        }
    }
}
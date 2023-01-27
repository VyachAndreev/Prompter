package com.example.prompter.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout

class InverterLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var rectHeight = 0

    var mirror = NONE
        set(value) {
            field = value
            invalidate()
        }
    private val paint: Paint = Paint()
    private val filter: ColorFilter

    init {
        filter = ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mirror != 0) {
            canvas?.let {
                when(mirror) {
                    HORIZONTAL -> {
                        mirrorHorizontal(it)
                    }
                    VERTICAL -> {
                        mirrorVertical(it)
                    }
                    BOTH -> {
                        mirrorBoth(it)
                    }
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas1 = Canvas(bitmap)
        super.dispatchDraw(canvas1)
        paint.colorFilter = null
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        paint.colorFilter = filter
        val rect = Rect(0, 0, width, rectHeight)
        canvas.drawBitmap(bitmap, rect, rect, paint)
    }

    private fun mirrorHorizontal(canvas: Canvas) {
        canvas.translate(width.toFloat(), 0f);
        canvas.scale(-1f, 1f);
    }

    private fun mirrorVertical(canvas: Canvas) {
        canvas.translate(0f, height.toFloat());
        canvas.scale(1f, -1f);
    }

    private fun mirrorBoth(canvas: Canvas) {
        mirrorHorizontal(canvas)
        mirrorVertical(canvas)
    }

    fun setInvertedHeight(height: Int) {
        rectHeight = (height * 1.3).toInt()
        invalidate()
    }

    companion object {
        const val NONE = 0
        const val HORIZONTAL = 1
        const val VERTICAL = 2
        const val BOTH = 3
    }
}
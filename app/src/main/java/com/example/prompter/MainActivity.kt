package com.example.prompter

import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Display
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.NestedScrollView
import com.example.prompter.view.InverterLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    private val scopeMain by lazy { CoroutineScope(Dispatchers.Main) }
    private val textSize: Float
        get() {
            return textContainer.textSize
        }

    private var speed = 1.0
    private var isRunning = false
    private var previousTime: Long? = null

    private lateinit var scrollView: NestedScrollView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var horizontalSwitcher: SwitchCompat
    private lateinit var verticalSwitcher: SwitchCompat
    private lateinit var inverter: InverterLayout
    private lateinit var textContainer: EditText
    private lateinit var decreaseSpeedButton: Button
    private lateinit var increaseSpeedButton: Button
    private lateinit var speedText: TextView
    private lateinit var lowerTextSizeButton: Button
    private lateinit var upperTextSizeButton: Button
    private lateinit var textSizeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val height = size.y

        textContainer = findViewById(R.id.text_container)
        scrollView = findViewById(R.id.scroll_view)
        playButton = findViewById(R.id.play_btn)
        pauseButton = findViewById(R.id.pause_btn)
        stopButton = findViewById(R.id.stop_btn)
        horizontalSwitcher = findViewById(R.id.horizontal_switcher)
        verticalSwitcher = findViewById(R.id.vertical_switcher)
        inverter = findViewById(R.id.inverter)
        decreaseSpeedButton = findViewById(R.id.decrease_speed_btn)
        increaseSpeedButton = findViewById(R.id.increase_speed_btn)
        speedText = findViewById(R.id.speed_textview)
        lowerTextSizeButton = findViewById(R.id.lower_text_size_btn)
        upperTextSizeButton = findViewById(R.id.upper_text_size_btn)
        textSizeText = findViewById(R.id.text_size_textview)

        textContainer.apply {
            setPadding(0, 0, 0, height)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    inverter.invalidate()
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
        }

        playButton.setOnClickListener {
            startScrolling()
        }
        pauseButton.setOnClickListener {
            stopScrolling()
        }
        stopButton.setOnClickListener {
            stopScrolling()
            scrollView.fullScroll(ScrollView.FOCUS_UP)
        }

        horizontalSwitcher.setOnCheckedChangeListener { _, b ->
            if (b) {
                inverter.mirror += InverterLayout.HORIZONTAL
            } else {
                inverter.mirror -= InverterLayout.HORIZONTAL
            }
        }

        verticalSwitcher.setOnCheckedChangeListener { _, b ->
            if (b) {
                inverter.mirror += InverterLayout.VERTICAL
            } else {
                inverter.mirror -= InverterLayout.VERTICAL
            }
        }

        decreaseSpeedButton.setOnClickListener {
            speed = max(MIN_SPEED, speed - DELTA_SPEED)
            updateSpeedText()
        }

        increaseSpeedButton.setOnClickListener {
            speed = min(MAX_SPEED, speed + DELTA_SPEED)
            updateSpeedText()
        }

        updateSpeedText()

        lowerTextSizeButton.setOnClickListener {
            textContainer.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                max(MIN_TEXT_SIZE, textSize - DELTA_TEXT_SIZE)
            )
            updateTextSizeText()
        }
        upperTextSizeButton.setOnClickListener {
            textContainer.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                min(MAX_TEXT_SIZE, textSize + DELTA_TEXT_SIZE)
            )
            updateTextSizeText()
        }

        updateTextSizeText()
    }

    private fun stopScrolling() {
        isRunning = false
        previousTime = null
    }

    private fun startScrolling() {
        isRunning = true
        scrollText()
    }

    private fun scrollText() {
        scopeMain.launch {
            val currentTime = SystemClock.uptimeMillis()
            if (previousTime != null) {
                val deltaTime = ((currentTime - previousTime!!) * 0.5 * speed).toInt()
                scrollView.scrollBy(0, deltaTime)
            }
            if (isRunning) {
                previousTime = currentTime
                scrollText()
            }
        }
    }

    private fun updateSpeedText() {
        speedText.text = speed.toString()
    }

    private fun updateTextSizeText() {
        textSizeText.text = textSize.toInt().toString()
        inverter.setInvertedHeight(textSize.toInt())
    }

    companion object {
        private const val DELTA_SPEED = 0.25
        private const val MIN_SPEED = 0.0
        private const val MAX_SPEED = 2.0
        private const val DELTA_TEXT_SIZE = 10.0f
        private const val MIN_TEXT_SIZE = 40.0f
        private const val MAX_TEXT_SIZE = 160.0f
    }
}
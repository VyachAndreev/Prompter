package com.example.prompter

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
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
import com.example.prompter.view.InverterLayout
import com.example.prompter.view.MyScrollView
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity(), MyScrollView.OnScrollChangedListener {
    private val textSize: Float
        get() {
            return textContainer.textSize
        }
    private val preferences: SharedPreferences
        get() {
            return getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    private val defaultTimeForLine: Double by lazy {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                DEFAULT_TIME_FOR_SCROLLING_LINE
            } else {
                LANDSCAPE_TIME_FOR_SCROLLING_LINE
            }
        }

    private var speed = 1.0

    private lateinit var scrollView: MyScrollView
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
            preferences.getString(TEXT_KEY, null)?.let { text ->
                setText(text)
            }
            setPadding(0, 0, 0, height)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    inverter.invalidate()
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
        }

        scrollView.setListener(this)

        playButton.setOnClickListener {
            scrollView.startScrolling()
        }
        pauseButton.setOnClickListener {
            scrollView.stopScrolling()
        }
        stopButton.setOnClickListener {
            scrollView.stopScrolling()
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
            updateSpeed()
        }

        increaseSpeedButton.setOnClickListener {
            speed = min(MAX_SPEED, speed + DELTA_SPEED)
            updateSpeed()
        }

        updateSpeed()

        lowerTextSizeButton.setOnClickListener {
            textContainer.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                max(MIN_TEXT_SIZE, textSize - DELTA_TEXT_SIZE)
            )
            updateTextSize()
        }
        upperTextSizeButton.setOnClickListener {
            textContainer.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                min(MAX_TEXT_SIZE, textSize + DELTA_TEXT_SIZE)
            )
            updateTextSize()
        }

        updateTextSize()
    }

    override fun onPause() {
        super.onPause()
        val editor = preferences.edit()
        editor.putString(TEXT_KEY, textContainer.text.toString())
        editor.apply()
    }

    override fun notifyNothingChanged() {
        scrollView.stopScrolling()
    }

    private fun updateSpeed() {
        updatePixelTime()
        speedText.text = speed.toString()
    }

    private fun updateTextSize() {
        updatePixelTime()
        textSizeText.text = textSize.toInt().toString()
        inverter.setInvertedHeight(textSize.toInt())
    }

    private fun updatePixelTime() {
        scrollView.timeForPixel = (defaultTimeForLine / (textSize * speed)).toLong()
    }

    companion object {
        private const val DEFAULT_TIME_FOR_SCROLLING_LINE = 1000.0
        private const val LANDSCAPE_TIME_FOR_SCROLLING_LINE = 1800.0
        private const val DELTA_SPEED = 0.25
        private const val MIN_SPEED = 0.25
        private const val MAX_SPEED = 2.0
        private const val DELTA_TEXT_SIZE = 10.0f
        private const val MIN_TEXT_SIZE = 40.0f
        private const val MAX_TEXT_SIZE = 160.0f
        private const val PREFS = "PREFS"
        private const val TEXT_KEY = "TEXT_KEY"
    }
}
package com.railtrack.india

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val API_BASE_URL = "https://YOUR-BACKEND-DOMAIN/api"

    private lateinit var input: EditText
    private lateinit var info: TextView

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(16))
            setBackgroundColor(Color.BLACK)
        }

        root.addView(
            TextView(this).apply {
                text = "RailTrack India"
                textSize = 28f
                setTextColor(Color.WHITE)
                setPadding(0, 0, 0, dp(4))
            },
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            TextView(this).apply {
                text = "LIVE TRAIN TRACKING"
                textSize = 13f
                setTextColor(Color.rgb(255, 109, 0))
                setPadding(0, 0, 0, dp(18))
            },
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        input = EditText(this).apply {
            setText("12002")
            hint = "Enter Train Number"
            isSingleLine = true
            textSize = 18f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setPadding(dp(16), 0, dp(16), 0)
            setBackgroundColor(Color.DKGRAY)
        }

        root.addView(
            input,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        val button = Button(this).apply {
            text = "TRACK LIVE"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(255, 109, 0))
            setOnClickListener { load() }
        }

        val buttonParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(56)
        )
        buttonParams.topMargin = dp(14)

        root.addView(button, buttonParams)

        info = TextView(this).apply {
            text = "Enter a train number and tap TRACK LIVE."
            textSize = 17f
            setTextColor(Color.WHITE)
            setPadding(0, dp(24), 0, 0)
        }

        root.addView(
            info,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            TextView(this).apply {
                text = "Orange • Black • White | Live API mode"
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
            },
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }

    private fun load() {
        val n = input.text.toString().trim()

        if (n.isEmpty()) {
            info.text = "Please enter a train number."
            return
        }

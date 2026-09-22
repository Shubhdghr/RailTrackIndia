package com.railtrack.india

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(20),
                dp(24),
                dp(20),
                dp(16)
            )
            setBackgroundColor(Color.BLACK)
        }

        val title = TextView(this).apply {
            text = "RailTrack India"
            textSize = 28f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, dp(4))
        }

        root.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this).apply {
            text = "LIVE TRAIN TRACKING"
            textSize = 13f
            setTextColor(Color.rgb(255, 109, 0))
            setPadding(0, 0, 0, dp(18))
        }

        root.addView(
            subtitle,
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
            setHintTextColor(Color.LTGRAY)
            setPadding(
                dp(16),
                0,
                dp(16),
                0
            )
            setBackgroundColor(Color.DKGRAY)
        }

        root.addView(
            input,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        val trackButton = Button(this).apply {
            text = "TRACK LIVE"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(255, 109, 0))
            setOnClickListener {
                load()
            }
        }

        val buttonParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(56)
        )

        buttonParams.topMargin = dp(14)

        root.addView(
            trackButton,
            buttonParams
        )

        info = TextView(this).apply {
            text = "Enter a train number and tap TRACK LIVE."
            textSize = 17f
            setTextColor(Color.WHITE)
            setPadding(
                0,
                dp(24),
                0,
                0
            )
        }

        root.addView(
            info,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val footer = TextView(this).apply {
            text = "Orange • Black • White | Live API mode"
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(Color.GRAY)
        }

        root.addView(
            footer,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }

    private fun load() {

        val trainNumber = input.text.toString().trim()

        if (trainNumber.isEmpty()) {
            info.text = "Please enter a train number."
            return
        }

        info.text = "Fetching live data..."

        thread {

            try {

                val connection =
                    URL("$API_BASE_URL/train/$trainNumber/live")
                        .openConnection() as HttpURLConnection

                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode

                val body = if (responseCode in 200..299) {
                    connection.inputStream
                        .bufferedReader()
                        .readText()
                } else {
                    connection.errorStream
                        ?.bufferedReader()
                        ?.readText()
                        ?: ""
                }

                val data =
                    JSONObject(body).optJSONObject("data")

                runOnUiThread {

                    if (data == null) {

                        info.text =
                            "Live data unavailable.\nHTTP $responseCode"

                        return@runOnUiThread
                    }

                    val train =
                        data.optJSONObject("train")

                    val location =
                        data.optJSONObject("currentLocation")

                    val trainName =
                        train?.optString(
                            "name",
                            "Train"
                        ) ?: "Train"

                    val trainNumberFromApi =
                        train?.optString(
                            "number",
                            trainNumber
                        ) ?: trainNumber

                    val status =
                        data.optString(
                            "status",
                            "unknown"
                        )

                    val station =
                        location?.optString(
                            "stationCode",
                            "—"
                        ) ?: "—"

                    val delay =
                        data.optInt(
                            "delayMinutes",
                            0
                        )

                    val updated =
                        data.optString(
                            "lastUpdatedAt",
                            "—"
                        )

                    info.text =
                        "$trainNumberFromApi • $trainName\n\n" +
                        "🟢 Status: $status\n" +
                        "📍 Current: $station\n" +
                        "⏱ Delay: $delay min\n" +
                        "🔄 Updated: $updated"
                }

                connection.disconnect()

            } catch (e: Exception) {

                runOnUiThread {

                    info.text =
                        "Cannot reach live server.\n" +
                        "Check internet/backend."
                }
            }
        }
    }
}

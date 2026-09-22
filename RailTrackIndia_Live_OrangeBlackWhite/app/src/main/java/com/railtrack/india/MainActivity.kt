package com.railtrack.india

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : Activity() {
 private val API_BASE_URL = "https://YOUR-BACKEND-DOMAIN/api"
 private lateinit var input: EditText
 private lateinit var info: TextView

 override fun onCreate(b: Bundle?) {
  super.onCreate(b)
  val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(24,28,24,20);setBackgroundColor(Color.BLACK)}
  root.addView(TextView(this).apply{text="RailTrack India";textSize=28f;setTextColor(Color.WHITE);setPadding(0,0,0,6)})
  root.addView(TextView(this).apply{text="LIVE TRAIN TRACKING";textSize=12f;setTextColor(Color.rgb(255,109,0))})
  input=EditText(this).apply{setText("12301");hint="Train number";singleLine=true;textSize=18f;setTextColor(Color.WHITE);setHintTextColor(Color.GRAY)}
  root.addView(input,LinearLayout.LayoutParams(-1,62))
  root.addView(Button(this).apply{text="TRACK LIVE";setTextColor(Color.WHITE);setBackgroundColor(Color.rgb(255,109,0));setOnClickListener{load()}},LinearLayout.LayoutParams(-1,58).apply{topMargin=12})
  info=TextView(this).apply{textSize=17f;setTextColor(Color.WHITE);setPadding(0,24,0,0)}
  root.addView(info,LinearLayout.LayoutParams(-1,0,1f))
  root.addView(TextView(this).apply{text="Orange • Black • White | Live API mode";textSize=12f;gravity=Gravity.CENTER;setTextColor(Color.GRAY)})
  setContentView(root)
 }
 private fun load(){
  val n=input.text.toString().trim()
  if(n.isEmpty()){info.text="Enter a train number.";return}
  info.text="Fetching live data..."
  thread{
   try{
    val c=URL("$API_BASE_URL/train/$n/live").openConnection() as HttpURLConnection
    c.connectTimeout=10000;c.readTimeout=10000
    val body=c.inputStream.bufferedReader().readText(); val d=JSONObject(body).optJSONObject("data")
    runOnUiThread{
     if(d==null){info.text="Live data unavailable (HTTP ${c.responseCode})";return@runOnUiThread}
     val t=d.optJSONObject("train"); val l=d.optJSONObject("currentLocation"); val nx=d.optJSONObject("nextHalt")
     val sp=l?.optDouble("speedKmh",Double.NaN)
     info.text="${t?.optString("number",n)} • ${t?.optString("name","Train")}\n\n🟢 ${d.optString("status","unknown")}\n📍 ${l?.optString("stationCode","—")}\n⚡ ${if(sp==null || sp.isNaN())"—" else "%.1f km/h".format(sp)}\n⏱ Delay: ${d.optInt("delayMinutes",0)} min\n➡️ Next: ${nx?.optString("stationName","—")}\n🔄 Updated: ${d.optString("lastUpdatedAt","—")}"
    }
   }catch(e:Exception){runOnUiThread{info.text="Cannot reach live server. Check backend URL/internet."}}
  }
 }
}

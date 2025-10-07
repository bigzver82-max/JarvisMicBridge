package ru.zverev.jarvismic
import android.Manifest
import android.os.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.content.*

class MainActivity: AppCompatActivity() {
  private lateinit var status: TextView; private lateinit var btn: Button; private lateinit var path: EditText
  private val req = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){}
  override fun onCreate(b: Bundle?) {
    super.onCreate(b); setContentView(R.layout.activity_main)
    status=findViewById(R.id.status); btn=findViewById(R.id.btn); path=findViewById(R.id.path)
    val sp=getSharedPreferences("mic", MODE_PRIVATE)
    path.setText(sp.getString("wav", MicService.DEFAULT_PATH))
    btn.setOnClickListener{ sp.edit().putString("wav", path.text.toString()).apply(); toggle() }
    req.launch(arrayOf(Manifest.permission.RECORD_AUDIO)); refresh()
  }
  private fun refresh(){ status.text="Mic: "+ if(MicService.running)"работает" else "остановлен"; btn.text= if(MicService.running)"Стоп" else "Старт" }
  private fun toggle(){ if(MicService.running) stopService(Intent(this,MicService::class.java))
    else startForegroundService(Intent(this,MicService::class.java))
    Handler(Looper.getMainLooper()).postDelayed({ refresh() },400) }
}

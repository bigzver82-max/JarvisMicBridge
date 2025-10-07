package ru.zverev.jarvismic
import android.app.*; import android.content.*; import android.media.*; import android.os.*; import androidx.core.app.NotificationCompat
import java.io.*
class MicService: Service() {
  companion object {
    @Volatile var running=false
    const val CH="jarvis_mic"; const val NID=7
    const val SAMPLE_RATE=16000; const val CHANNEL=AudioFormat.CHANNEL_IN_MONO; const val ENCODING=AudioFormat.ENCODING_PCM_16BIT
    const val DEFAULT_PATH="/data/data/com.termux/files/home/jarvis_core/tmp/jarvis_stream.wav"
  }
  private var t: Thread?=null
  override fun onCreate(){ super.onCreate(); running=true; fg(); rec() }
  override fun onDestroy(){ running=false; t?.interrupt(); super.onDestroy() }
  override fun onBind(i: Intent?)=null
  private fun fg(){ val nm=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    if(nm.getNotificationChannel(CH)==null) nm.createNotificationChannel(NotificationChannel(CH,"Jarvis Mic",NotificationManager.IMPORTANCE_LOW))
    val n=NotificationCompat.Builder(this,CH).setOngoing(true).setSmallIcon(android.R.drawable.ic_btn_speak_now)
      .setContentTitle("Jarvis Mic Bridge").setContentText("микрофон активен").build()
    startForeground(NID,n) }
  private fun rec(){
    val prefs=getSharedPreferences("mic", MODE_PRIVATE); val outPath=prefs.getString("wav", DEFAULT_PATH)?:DEFAULT_PATH
    File(outPath).parentFile?.mkdirs()
    t=Thread{
      val min=AudioRecord.getMinBufferSize(SAMPLE_RATE,CHANNEL,ENCODING)
      val ar=AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,SAMPLE_RATE,CHANNEL,ENCODING,min*2)
      val buf=ByteArray(min); var raf:RandomAccessFile?=null
      try{
        raf=RandomAccessFile(outPath,"rw"); wavHdr(raf,SAMPLE_RATE,1,16)
        ar.startRecording()
        while(!Thread.currentThread().isInterrupted){ val n=ar.read(buf,0,buf.size); if(n>0) raf.write(buf,0,n) }
        ar.stop()
      }catch(_:Exception){} finally { try{ar.release()}catch(_:Exception){}; try{raf?.close()}catch(_:Exception){} }
    }; t!!.start()
  }
  private fun wavHdr(raf:RandomAccessFile, sr:Int, ch:Int, bits:Int){
    fun w(s:String)=raf.write(s.toByteArray()); fun d(i:Int)=raf.write(byteArrayOf((i and 0xFF).toByte(),((i shr 8) and 0xFF).toByte(),((i shr 16) and 0xFF).toByte(),((i shr 24) and 0xFF).toByte()))
    fun w2(i:Int)=raf.write(byteArrayOf((i and 0xFF).toByte(),((i shr 8) and 0xFF).toByte()))
    w("RIFF"); d(0x7FFFFFFF); w("WAVE"); w("fmt "); d(16); w2(1); w2(ch); d(sr); d(sr*ch*bits/8); w2(ch*bits/8); w2(bits); w("data"); d(0x7FFFFFFF)
  }
}

package ru.zverev.jarvismic

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import java.io.File

class MicService : Service() {
    private var recorder: MediaRecorder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val file = File(cacheDir, "mic_test.wav")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        recorder?.stop()
        recorder?.release()
        recorder = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

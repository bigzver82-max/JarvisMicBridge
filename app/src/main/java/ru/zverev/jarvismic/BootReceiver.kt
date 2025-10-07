package ru.zverev.jarvismic
import android.content.*
class BootReceiver: BroadcastReceiver(){ override fun onReceive(c: Context, i: Intent){ c.startForegroundService(Intent(c, MicService::class.java)) } }

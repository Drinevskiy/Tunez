package com.example.tunez.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tunez.R

//@Composable
//fun MusicNotificationExample() {
//    val context = LocalContext.current
//    val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    // Создание канала уведомлений (обязательно с Android 8.0+)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val channel = NotificationChannel(
//            "music_channel_id",
//            "Music Controls",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    // Построение уведомления с действиями управления музыкой
//    val prevIntent = Intent(context, PlayerReceiver::class.java).apply {
//        action = "PREV"
//    }
//    val prevPendingIntent =
//        PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)
//
//    val playPauseIntent = Intent(context, PlayerReceiver::class.java).apply {
//        action = "PLAY_PAUSE"
//    }
//    val playPausePendingIntent =
//        PendingIntent.getBroadcast(context, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
//
//    val nextIntent = Intent(context, PlayerReceiver::class.java).apply {
//        action = "NEXT"
//    }
//    val nextPendingIntent =
//        PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)
//
//    val notification = NotificationCompat.Builder(context, "music_channel_id")
////        .setSmallIcon(R.drawable.)
//        .setContentTitle("Playing: Song Title")
//        .setContentText("Artist Name")
//        .addAction(R.drawable.baseline_pause_24, "Previous", prevPendingIntent)
//        .addAction(R.drawable.baseline_pause_24, "Play/Pause", playPausePendingIntent)
//        .addAction(R.drawable.baseline_pause_24, "Next", nextPendingIntent)
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        .build()
//
//    // Показ уведомления
//    notificationManager.notify(0, notification)
//}


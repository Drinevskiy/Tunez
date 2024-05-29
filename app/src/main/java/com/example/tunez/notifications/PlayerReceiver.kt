package com.example.tunez.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PlayerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "PREV" -> {
                // Обработка действия "Предыдущая"
                // Например, вызов функции предыдущего трека в вашем музыкальном плеере
            }
            "PLAY_PAUSE" -> {
                // Обработка действия "Воспроизвести/Пауза"
                // Например, вызов функции воспроизведения/паузы в вашем музыкальном плеере
            }
            "NEXT" -> {
                // Обработка действия "Следующая"
                // Например, вызов функции следующего трека в вашем музыкальном плеере
            }
        }
    }
}
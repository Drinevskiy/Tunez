package com.example.tunez.utils

import com.example.tunez.viewmodels.NavControllerViewModel
import org.koin.dsl.module

val appModule = module {
    single { NavControllerViewModel() }
    // Добавьте другие зависимости, которые вам нужны
}
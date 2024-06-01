package com.example.tunez.utils

import android.content.Context
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

//Dependency injection
val appModule = module {
    single<SpotifyService> { SpotifyService() }
    single<BaseActivity> { MainActivity() }
    single<NavControllerViewModel> { NavControllerViewModel() }
    single<ProfileViewModel> { ProfileViewModel(get()) }
//    single { provideBaseActivity() }
//    single { provideApplication(androidContext()) as BaseActivity } // Добавлено
//    viewModel { HomeViewModel(get(), get())}
//    single { SpotifyBroadcastReceiver(get()) }
//    single { SpotifyImplicitLoginActivityImpl() }
}

//fun provideBaseActivity(): BaseActivity {
//    return MainActivity()
//}
//
//fun provideApplication(context: Context): SpotifyPlaygroundApplication { // Новая функция
//    return context.applicationContext as SpotifyPlaygroundApplication
//}
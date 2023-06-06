package com.example.pill_good.di

import com.example.pill_good.repository.*
import com.example.pill_good.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    /**
     * Repository
     */
    factory { UserRepositoryImpl(get()) }
    factory { LoginRepositoryImpl(get()) }
    factory { GroupMemberRepositoryImpl(get()) }
    factory { PrescriptionRepositoryImpl(get()) }
    factory { PillRepositoryImpl(get()) }
    factory { TakePillRepositoryImpl(get()) }
    factory { NotificationRepositoryImpl(get()) }
    factory { SendAutoMessageRepositoryImpl(get()) }
    factory { OCRRepositoryImpl(get()) }

    /**
     * ViewModel
     */
    single { MainViewModel(get(), get(), get()) }
    single { GroupViewModel(get()) }
    single { PrescriptionViewModel(get()) }
    viewModel { PillViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { EditOCRViewModel(get()) }
    viewModel { CameraResultViewModel(get()) }
}

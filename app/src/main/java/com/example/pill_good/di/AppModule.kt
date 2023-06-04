package com.example.pill_good.di

import com.example.pill_good.repository.GroupMemberRepositoryImpl
import com.example.pill_good.repository.LoginRepositoryImpl
import com.example.pill_good.repository.NotificationRepositoryImpl
import com.example.pill_good.repository.PillRepositoryImpl
import com.example.pill_good.repository.PrescriptionRepositoryImpl
import com.example.pill_good.repository.SendAutoMessageRepositoryImpl
import com.example.pill_good.repository.TakePillRepositoryImpl
import com.example.pill_good.repository.UserRepositoryImpl
import com.example.pill_good.ui.viewmodel.*
import org.koin.dsl.module


val appModule = module {
    /**
     * Repository
     */
    factory { UserRepositoryImpl(get()) }
    factory { LoginRepositoryImpl(get())}
    factory { GroupMemberRepositoryImpl(get()) }
    factory { PrescriptionRepositoryImpl(get()) }
    factory { PillRepositoryImpl(get()) }
    factory { TakePillRepositoryImpl(get()) }
    factory { NotificationRepositoryImpl(get()) }
    factory { SendAutoMessageRepositoryImpl(get()) }

    /**
     * ViewModel
     */
    single { MainViewModel(get(), get()) }
    single { GroupViewModel(get()) }
    single { PrescriptionViewModel(get()) }
    single { PillViewModel(get()) }
    single { NotificationViewModel(get()) }
    single { EditOCRViewModel(get()) }
}


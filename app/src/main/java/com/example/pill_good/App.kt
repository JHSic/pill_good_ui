package com.example.pill_good

import android.app.Application
import com.example.pill_good.di.NetworkModule.networkModule
import com.example.pill_good.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    /**
     * Koin
     * 모듈화된 의존성들을  startKoin {} 내부에 선언해주면
     * Koin Framework에서 이를 관리해줌
     */
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(networkModule)
            modules(appModule)
        }
    }

}

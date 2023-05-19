package com.example.pill_good.di

import com.example.pill_good.data.remote.ApiService
import com.example.pill_good.repository.GroupMemberRepositoryImpl
import com.example.pill_good.repository.NotificationRepositoryImpl
import com.example.pill_good.repository.PillRepositoryImpl
import com.example.pill_good.repository.PrescriptionRepositoryImpl
import com.example.pill_good.repository.SendAutoMessageRepositoryImpl
import com.example.pill_good.repository.TakePillRepositoryImpl
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.inject

// 의존성을 주입할 예시 클래스
class MyClass(private val value: String) {
    fun getValue(): String {
        return value
    }
}

class DITest {
    @Before
    fun `create modules and startKoin`() {
        // Koin 모듈 생성

        /**
         * Example code_
         *
         * val myModule = module {
         *      single { MyClass("Hello, Koin!") }
         * }
         *
         */

        // Koin 시작
        startKoin {
            modules(appModule, NetworkModule.networkModule)
        }
    }

    @After
    fun `stop koin`() {
        // Koin 종료
        stopKoin()
    }

    @Test
    fun `app module dependency injection test`() {
        // 의존성 주입된 객체 가져오기
        val groupMemberRepositoryImpl: GroupMemberRepositoryImpl by inject(GroupMemberRepositoryImpl::class.java)
        val prescriptionRepositoryImpl: PrescriptionRepositoryImpl by inject(PrescriptionRepositoryImpl::class.java)
        val pillRepositoryImpl: PillRepositoryImpl by inject(PillRepositoryImpl::class.java)
        val takePillRepositoryImpl: TakePillRepositoryImpl by inject(TakePillRepositoryImpl::class.java)
        val notificationRepositoryImpl: NotificationRepositoryImpl by inject(NotificationRepositoryImpl::class.java)
        val sendAutoMessageRepositoryImpl: SendAutoMessageRepositoryImpl by inject(SendAutoMessageRepositoryImpl::class.java)

        println(groupMemberRepositoryImpl.javaClass)
        println(prescriptionRepositoryImpl.javaClass)
        println(pillRepositoryImpl.javaClass)
        println(takePillRepositoryImpl.javaClass)
        println(notificationRepositoryImpl.javaClass)
        println(sendAutoMessageRepositoryImpl.javaClass)
    }

    @Test
    fun `network module dependency injection test`() {
        // 의존성 주입된 객체 가져오기
        val apiService: ApiService by inject(ApiService::class.java)
        
    }


}
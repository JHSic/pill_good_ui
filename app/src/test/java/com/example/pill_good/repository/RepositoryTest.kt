package com.example.pill_good.repository

import com.example.pill_good.data.remote.ApiService
import com.example.pill_good.di.NetworkModule.gson
import com.example.pill_good.di.appModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Dong-hui, Kim
 * Todo - RepositoryTest 에 이용 되는 SuperClass
 *
 */
open class RepositoryTest {
    protected lateinit var mockWebServer: MockWebServer
    private lateinit var mockWebServerURL: String

    private val SERVER_URL = "http://192.168.0.2:8080"
    private val TEST_MODE = false    // true = MockWebServer, false = RealServer

    /**
     * TODO - MockWebServer 로 ApiService 의존성을 생성해 Koin 에 등록
     */
    @Before
    fun `setUp`() {
        var apiService: ApiService
        if (TEST_MODE) {
            apiService = getMockApiService()
        } else {
            mockWebServer = MockWebServer()
            apiService = getApiService()
        }

        startKoin {
            modules(module{ single { apiService } }, appModule)
        }
    }

    /**
     * TODO - MockWebServer 종료 후 Koin framework 종료
     */
    @After
    fun `tearDown`() {
        mockWebServer.shutdown()
        stopKoin()
    }

    /**
     * TODO - 현재 running 중인 MockWebServer 의 URL 정보 및 오래된 Request 의 정보를 출력
     *
     * @param mockWebServer Instance of running MockWebServer
     */
    protected fun printMockWebServerInfo(mockWebServer: MockWebServer) {
        println("MockWebServer URL: $mockWebServerURL")

        val request = mockWebServer.takeRequest()
        println("Last Request: ${request.method} ${request.path}")
        println("Request Headers: ${request.headers}")
        println("Request Body: ${request.body}")
    }

    /**
     * TODO - MockWebServer 의 url 정보로 retrofit 객체를 생성 후 이를 통해 ApiService 를 생성 반환
     *
     * @return Instance of ApiService
     */
    private fun getMockApiService(): ApiService {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServerURL = mockWebServer.url("").toString()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(mockWebServerURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }

    private fun getApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // 로깅 레벨을 BODY로 설정하여 요청/응답 내용을 출력
            })
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
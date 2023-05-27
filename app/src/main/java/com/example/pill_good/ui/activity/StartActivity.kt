package com.example.pill_good.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.pill_good.R

class StartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            //일정 시간 지연 후 MainActivity로 이동
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)

            //이전 키, 즉 취소 키를 눌렀을 때 activity_start 즉, 스플래시 스크린으로 이동 방지
            finish()
        }, 800) //0.8초 딜레이
    }
}
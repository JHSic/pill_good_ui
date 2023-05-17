package com.example.pill_good.ui.activity

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pill_good.R

class pillInformationActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pill_information)

        // 스크롤뷰 변경 테스트
        val closeButton : ImageButton = findViewById(R.id.pill_information_close_btn) // 닫기 버튼
        val pillInformationName : TextView = findViewById(R.id.pill_information_name) // 약 정보 화면 약 이름
        val pillEffectInformation : TextView = findViewById(R.id.pill_information_effect_content) // 약 정보 화면 약 효능
        val pillPrecautionInformation : TextView = findViewById(R.id.pill_information_precaution_content) // 약 정보 화면 약 주의사항


        closeButton.setOnClickListener {
            finish()
        }
    }
}
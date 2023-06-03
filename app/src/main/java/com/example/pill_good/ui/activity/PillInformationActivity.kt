package com.example.pill_good.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pill_good.R
import com.example.pill_good.data.dto.PillDTO
import com.google.firebase.storage.FirebaseStorage

class PillInformationActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pill_information)

        val pillInformation = intent.getSerializableExtra("pillInformationData") as PillDTO

        // 스크롤뷰 변경 테스트
        val closeButton : ImageButton = findViewById(R.id.pill_information_close_btn) // 닫기 버튼
        val pillInformationName : TextView = findViewById(R.id.pill_information_name) // 약 정보 화면 약 이름
        val pillInformationEffect : TextView = findViewById(R.id.pill_information_effect_content) // 약 정보 화면 약 효능
        val pillInformationPrecaution : TextView = findViewById(R.id.pill_information_precaution_content) // 약 정보 화면 약 주의사항
        val pillInformationShape : TextView = findViewById(R.id.pill_information_shape) // 약 모양
        val pillInformationColor : TextView = findViewById(R.id.pill_information_color) // 약 색깔
        val pillInformationFormulation : TextView = findViewById(R.id.pill_information_formulation) // 약 형질

        pillInformationName.text = pillInformation.pillName
        pillInformationEffect.text = pillInformation.pillEffect
        pillInformationPrecaution.text = pillInformation.pillPrecaution
        pillInformationShape.text = pillInformation.pillShape
        pillInformationColor.text = pillInformation.pillColor
        pillInformationFormulation.text = pillInformation.pillFormulation
        setPillImage(pillInformation.pillNum!!)

        closeButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
        }
    }

    private fun setPillImage(pillNum : String){
        // Firebase 이미지 로드 초기 설정
        val storageRef = FirebaseStorage.getInstance().reference
        val imageView: ImageView = findViewById(R.id.pill_information_image)

        // 이미지 경로 지정
        val imageRef = storageRef.child("$pillNum.jpg")
        // 이미지 다운로드 URL 가져오기
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // 다운로드 URL을 사용하여 이미지 설정
            Glide.with(this)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            // 다운로드 실패 시 처리할 작업
            Log.e("TAG", "이미지 다운로드 실패: ${exception.message}")
        }
    }
}
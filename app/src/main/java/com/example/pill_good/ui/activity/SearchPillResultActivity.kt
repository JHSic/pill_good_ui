package com.example.pill_good.ui.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import com.example.pill_good.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class SearchPillResultActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_search_pill_result)

        val inflater = LayoutInflater.from(this)

        val linearLayout = findViewById<LinearLayout>(R.id.pill_search_result_linear)

        var pillNum = 5
        for(i in 1..pillNum){
            val pillContent: FrameLayout = inflater.inflate(R.layout.activity_pill_item, null) as FrameLayout
            val pillLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            pillLayoutParams.setMargins(32, 0, 32, 32) // 아래쪽에 32dp의 마진
            pillContent.layoutParams = pillLayoutParams

            pillContent.setOnClickListener {
                val intent = Intent(this,pillInformationActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
            }
            linearLayout.addView(pillContent)
        }
    }

    // 기존에 작성했던 레이아웃을 통한 동적 생성이 아닌 코틀린을 통한 동적 생성
    // 임시로 미삭제
//    private fun generateCardView(): LinearLayout {
//
//        val pillImage = ImageView(this)
//        // 테스트 코드 삭제해도 됨
////        // 파이어베이스 스토리지 참조
////        val storageReference = FirebaseStorage.getInstance().reference
////
////        // 이미지 다운로드 URL 가져오기
////        val imageRef = storageReference.child("gs://pillgood-4128c.appspot.com/195900043.jpg")
////        imageRef.downloadUrl.addOnSuccessListener { uri ->
////            val imageURL = uri.toString()
////
////            // 이미지를 다운로드하여 ImageView에 설정
////            Picasso.get().load(imageURL).into(pillImage)
////        }.addOnFailureListener { exception ->
////            // 다운로드 실패 처리
////            Log.e("Firebase", "Failed to download image: $exception")
////        }
//
//
//        val cardLinearLayout = LinearLayout(this)
//        cardLinearLayout.orientation = LinearLayout.HORIZONTAL
//
//        val imagePath = R.drawable.a201412020003201
//        val bitmap = BitmapFactory.decodeResource(resources, imagePath)
//
//        pillImage.setImageBitmap(bitmap)
//        pillImage.layoutParams = LinearLayout.LayoutParams(300, 150)
//
//        val pillName = TextView(this)
//        pillName.text = "엑스콜골드"
//        pillName.gravity = Gravity.CENTER
//        pillName.textSize = 15f
//        pillName.fontFeatureSettings = "@font/pt_sans_bold"
//        pillName.setTextColor(Color.BLACK)
//        pillName.setPadding(25, 40, 25, 40)
//        pillName.ellipsize = TextUtils.TruncateAt.END
//        pillName.maxLines = 1
//
//        cardLinearLayout.addView(pillImage)
//        cardLinearLayout.addView(pillName)
//
//        cardLinearLayout.setOnClickListener {
//            val intent = Intent(this,pillInformationActivity::class.java)
//            startActivity(intent)
//            overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
//        }
//
//        return cardLinearLayout
//    }
}
package com.example.pill_good.ui.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import com.example.pill_good.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class SearchPillResultActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_search_pill_result)

        val linearLayout = findViewById<LinearLayout>(R.id.pill_search_result_linear)

        // 알림 개수 만큼 생성되게 설정
        for(i: Int in 1..5) {
            // Initialize a new LayoutParams instance, CardView width and height
            val layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // CardView width
                ViewGroup.LayoutParams.WRAP_CONTENT // CardView height
            )

            layoutParams.setMargins(64,24,64,28)

            // 카드뷰 생성
            val searchResultCard = CardView(this)

            // layout params 설정
            searchResultCard.layoutParams = layoutParams

            // 둥근 정도 설정
            searchResultCard.radius = 40F

            // 패딩 설정
            searchResultCard.setContentPadding(25,0,25,0)

            // 배경색 설정
            searchResultCard.setCardBackgroundColor(Color.WHITE)

            // 그림자
            searchResultCard.cardElevation = 8F

            // 그림자 최대치
            searchResultCard.maxCardElevation = 12F

            // 카드뷰 내용 생성
            searchResultCard.addView(generateCardView())

            // linearLayout에 카드뷰 추가
            linearLayout.addView(searchResultCard)
        }
    }

    // Custom method to generate an image view
    private fun generateCardView(): LinearLayout {

        val pillImage = ImageView(this)
        // 테스트 코드 삭제해도 됨
//        // 파이어베이스 스토리지 참조
//        val storageReference = FirebaseStorage.getInstance().reference
//
//        // 이미지 다운로드 URL 가져오기
//        val imageRef = storageReference.child("gs://pillgood-4128c.appspot.com/195900043.jpg")
//        imageRef.downloadUrl.addOnSuccessListener { uri ->
//            val imageURL = uri.toString()
//
//            // 이미지를 다운로드하여 ImageView에 설정
//            Picasso.get().load(imageURL).into(pillImage)
//        }.addOnFailureListener { exception ->
//            // 다운로드 실패 처리
//            Log.e("Firebase", "Failed to download image: $exception")
//        }


        val cardLinearLayout = LinearLayout(this)
        cardLinearLayout.orientation = LinearLayout.HORIZONTAL

        val imagePath = R.drawable.a201412020003201
        val bitmap = BitmapFactory.decodeResource(resources, imagePath)

        pillImage.setImageBitmap(bitmap)
        pillImage.layoutParams = LinearLayout.LayoutParams(300, 150)

        val textLinearLayout = LinearLayout(this)
        textLinearLayout.orientation = LinearLayout.VERTICAL

        val pillName = TextView(this)
        pillName.text = "엑스콜골드"
        pillName.gravity = Gravity.CENTER
        pillName.textSize = 15f
        pillName.fontFeatureSettings = "@font/pt_sans_bold"
        pillName.setTextColor(Color.BLACK)

        textLinearLayout.addView(pillName)

        cardLinearLayout.addView(pillImage)
        cardLinearLayout.addView(textLinearLayout)
        
        cardLinearLayout.setOnClickListener {
            val intent = Intent(this,pillInformationActivity::class.java)
            startActivity(intent)
        }

        return cardLinearLayout
    }
}
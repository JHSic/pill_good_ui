package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.bumptech.glide.Glide
import com.example.pill_good.R
import com.example.pill_good.data.dto.PillDTO
import com.example.pill_good.data.dto.SearchingConditionDTO
import com.example.pill_good.ui.viewmodel.PillViewModel
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchPillResultActivity : CustomActionBarActivity() {
    private val pillViewModel : PillViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomView(R.layout.activity_search_pill_result)

        val searchPillData = intent.getSerializableExtra("pillData") as SearchingConditionDTO

        pillViewModel.loadPillData(searchPillData)

        pillViewModel.pillData.observe(this) { _pillData ->
            if (_pillData != null) {
                generatePillResult(_pillData)
            }
        }
    }

    fun generatePillResult(pillData : List<PillDTO>){
        val inflater = LayoutInflater.from(this)

        val linearLayout = findViewById<LinearLayout>(R.id.pill_search_result_linear)

        val countPill : TextView = findViewById(R.id.search_pill_num)

        countPill.text = "${pillData.size}건"

        // Firebase 이미지 로드 초기 설정
        val storageRef = FirebaseStorage.getInstance().reference

        for(i in pillData.indices){
            val pillContent: FrameLayout = inflater.inflate(R.layout.activity_pill_item, null) as FrameLayout
            val pillLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            val pillItemName : TextView = pillContent.findViewById(R.id.pill_item_name)
            val pillItemFeatureShape : TextView = pillContent.findViewById(R.id.pill_item_feature_shape)
            val pillItemFeatureColor : TextView = pillContent.findViewById(R.id.pill_item_feature_color)
            val pillItemImage : ImageView = pillContent.findViewById(R.id.pill_item_image)
            pillLayoutParams.setMargins(32, 0, 32, 32) // 아래쪽에 32dp의 마진
            pillContent.layoutParams = pillLayoutParams

            pillItemName.text = pillData[i].pillName
            pillItemFeatureShape.text = pillData[i].pillShape
            pillItemFeatureColor.text = pillData[i].pillColor

            // 이미지 경로 지정
            val imageRef = storageRef.child(pillData[i].pillNum + ".jpg")
            // 이미지 다운로드 URL 가져오기
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // 다운로드 URL을 사용하여 이미지 설정
                Glide.with(this)
                    .load(uri)
                    .into(pillItemImage)
            }.addOnFailureListener { exception ->
                // 다운로드 실패 시 처리할 작업
                Log.e("TAG", "이미지 다운로드 실패: ${exception.message}")
            }

            pillContent.setOnClickListener {
                val intent = Intent(this,PillInformationActivity::class.java)
                intent.putExtra("pillInformationData", pillData[i])
                startActivity(intent)
                overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
            }

            linearLayout.addView(pillContent)
        }
    }
}
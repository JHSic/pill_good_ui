package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import com.example.pill_good.R
import com.example.pill_good.data.dto.SearchingConditionDTO

class SearchPillActivity : CustomActionBarActivity() {
    private var selectedPillColor: String? = null
    private var selectedPillShape: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_search_pill)

        val pillName : EditText = findViewById(R.id.search_pill_name)
        val pillCarve : EditText = findViewById(R.id.search_pill_carve)

        var pillFrontWord : String? = null
        var pillBackWord : String? = null
        setupPillColorSelection()
        setupPillShapeSelection()

        // 각인 위치 및 값 설정
        val wordRadioGroup : RadioGroup = findViewById(R.id.text_radio_group)
        wordRadioGroup.setOnCheckedChangeListener{ _, btnId ->
            when(btnId){
                R.id.text_front_radio -> {
                    pillFrontWord = pillCarve.text.toString()
                }
                R.id.text_back_radio -> {
                    pillBackWord = pillCarve.text.toString()
                }
            }
        }



        val searchButton: Button = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            if(pillName.text.toString().isNullOrEmpty() && pillFrontWord.toString().isNullOrEmpty() &&
                pillBackWord.toString().isNullOrEmpty() && selectedPillColor.isNullOrEmpty() && selectedPillShape.isNullOrEmpty()){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("검색할 약의 정보를 입력하세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    // 그냥 팝업만 닫음
                }
                val dialog = builder.create()
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                }
                dialog.show()

            }
            else{
                val searchPillData = SearchingConditionDTO(
                    pillName = pillName.text.toString(),
                    pillFrontWord = pillFrontWord,
                    pillBackWord = pillBackWord,
                    pillShape = selectedPillShape,
                    pillColor = selectedPillColor
                )
                println("12312321312" + selectedPillShape)
                println("asdasdasdasdasdsa" + selectedPillColor)
                println("asdasdasdasdasasd" + pillName.text.toString())
                var searchPillResultIntent = Intent(this, SearchPillResultActivity::class.java)
                searchPillResultIntent.putExtra("pillData", searchPillData)
                startActivity(searchPillResultIntent)
                overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
            }
        }
    }
    private fun setupPillColorSelection() {
        val colorRadioGroup: RadioGroup = findViewById(R.id.color_radio_group)
        colorRadioGroup.setOnCheckedChangeListener { _, btnId ->
            selectedPillColor = when (btnId) {
                R.id.white_radio -> "흰색"
                R.id.pink_radio -> "분홍"
                R.id.yellow_radio -> "노랑"
                R.id.orange_radio -> "주황"
                R.id.brown_radio -> "갈색"
                R.id.blue_radio -> "파랑"
                R.id.light_green_radio -> "연두"
                R.id.green_radio -> "초록"
                R.id.red_radio -> "빨강"
                R.id.gray_radio -> "회색"
                R.id.etc_color_radio -> "기타"
                else -> null
            }
        }
    }

    private fun setupPillShapeSelection() {
        val shapeRadioGroup: RadioGroup = findViewById(R.id.shape_radio_group)
        shapeRadioGroup.setOnCheckedChangeListener { _, btnId ->
            selectedPillShape = when (btnId) {
                R.id.circle_radio -> "원형"
                R.id.quadrangle_radio -> "장방형"
                R.id.rectangle_radio -> "사각형"
                R.id.octagon_radio -> "팔각형"
                R.id.ellipse_radio -> "타원형"
                R.id.half_circle_radio -> "반원형"
                R.id.triangle_radio -> "삼각형"
                R.id.diamond_radio -> "마름모형"
                R.id.polygon_radio -> "오각형"
                R.id.hexagon_radio -> "육각형"
                R.id.etc_shape_radio -> "기타"
                else -> null
            }
        }
    }


}
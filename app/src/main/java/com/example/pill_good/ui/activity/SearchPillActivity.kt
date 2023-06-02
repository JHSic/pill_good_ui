package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import com.example.pill_good.R
import com.example.pill_good.data.dto.SearchingConditionDTO

class SearchPillActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_search_pill)

        val pillName : EditText = findViewById(R.id.search_pill_name)
        val pillCarve : EditText = findViewById(R.id.search_pill_carve)


        var pillColor = selectPillColor()
        var pillShape = selectPillShape()
        var pillFrontWord : String? = null
        var pillBackWord : String? = null

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

        val searchPillData = SearchingConditionDTO(
            pillName = pillName.text.toString(),
            pillFrontWord = pillFrontWord,
            pillBackWord = pillBackWord,
            pillShape = pillShape,
            pillColor = pillColor
        )

        val searchButton: Button = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            var searchPillResultIntent = Intent(this, SearchPillResultActivity::class.java)
            searchPillResultIntent.putExtra("pillData", searchPillData)
            startActivity(searchPillResultIntent)
            overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
        }
    }

    // 약 색깔 선택
    private fun selectPillColor() : String?{
        var searchPillColor: String? = null

        val colorRadioGroup : RadioGroup = findViewById(R.id.color_radio_group)
        colorRadioGroup.setOnCheckedChangeListener{ _, btnId ->
            when(btnId){
                R.id.white_radio -> {
                    searchPillColor = "원형"
                }
                R.id.pink_radio -> {
                    searchPillColor = "장방형"
                }
                R.id.yellow_radio -> {
                    searchPillColor = "사각형"
                }
                R.id.orange_radio -> {
                    searchPillColor = "팔각형"
                }
                R.id.brown_radio -> {
                    searchPillColor = "타원형"
                }
                R.id.blue_radio -> {
                    searchPillColor = "반원형"
                }
                R.id.light_green_radio -> {
                    searchPillColor = "삼각형"
                }
                R.id.green_radio -> {
                    searchPillColor = "마름모형"
                }
                R.id.red_radio -> {
                    searchPillColor = "오각형"
                }
                R.id.gray_radio -> {
                    searchPillColor = "육각형"
                }
                R.id.etc_color_radio -> {
                    searchPillColor = "기타"
                }
            }
        }
        return searchPillColor
    }

    // 약 모양 선택
    private fun selectPillShape() : String?{
        var searchPillShape: String? = null

        val shapeRadioGroup : RadioGroup = findViewById(R.id.shape_radio_group)
        shapeRadioGroup.setOnCheckedChangeListener{ _, btnId ->
            when(btnId){
                R.id.circle_radio -> {
                    searchPillShape = "원형"
                }
                R.id.quadrangle_radio -> {
                    searchPillShape = "장방형"
                }
                R.id.rectangle_radio -> {
                    searchPillShape = "사각형"
                }
                R.id.octagon_radio -> {
                    searchPillShape = "팔각형"
                }
                R.id.ellipse_radio -> {
                    searchPillShape = "타원형"
                }
                R.id.half_circle_radio -> {
                    searchPillShape = "반원형"
                }
                R.id.triangle_radio -> {
                    searchPillShape = "삼각형"
                }
                R.id.diamond_radio -> {
                    searchPillShape = "마름모형"
                }
                R.id.polygon_radio -> {
                    searchPillShape = "오각형"
                }
                R.id.hexagon_radio -> {
                    searchPillShape = "육각형"
                }
                R.id.etc_shape_radio -> {
                    searchPillShape = "기타"
                }
            }
        }
        return searchPillShape
    }
}
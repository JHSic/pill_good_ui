package com.example.pill_good.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginTop
import com.example.pill_good.R

class PrescriptionActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_prescription)

        val inflater = LayoutInflater.from(this)
//        val prescriptionContentLayout = inflater.inflate(R.layout.activity_prescription_content, null) as FrameLayout
//        val prescriptionPillContentLayout = inflater.inflate(R.layout.activity_prescription_pill_content, null) as FrameLayout

        val prescriptionContainer: LinearLayout = findViewById(R.id.prescription_linear)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)



        val numContents = 2 // 추가할 prescriptionContent 개수 - 처방전 개수
        for (i in 1..numContents) {
            val prescriptionContent: FrameLayout = inflater.inflate(R.layout.activity_prescription_content, null) as FrameLayout
            val prescriptionPillFrame: LinearLayout = prescriptionContent.findViewById(R.id.prescription_pill_data)
            layoutParams.setMargins(0, 0, 0, 16) // 아래쪽에 16dp의 마진
            prescriptionContent.layoutParams = layoutParams


            val numPillContents = 3 // prescriptionPillContent 개수 - 약 개수
            for (j in 1..numPillContents) {
                val prescriptionPillContent: FrameLayout = inflater.inflate(R.layout.activity_prescription_pill_content, null) as FrameLayout
                prescriptionPillFrame.addView(prescriptionPillContent)
                layoutParams.setMargins(0, 0, 0, 32) // 아래쪽에 16dp의 마진
                prescriptionPillContent.layoutParams = layoutParams
            }

            prescriptionContainer.addView(prescriptionContent)
        }


    }
}
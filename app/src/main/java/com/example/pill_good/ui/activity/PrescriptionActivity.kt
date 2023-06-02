package com.example.pill_good.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import com.example.pill_good.R
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.PrescriptionAndDiseaseNameDTO
import com.example.pill_good.ui.viewmodel.PrescriptionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrescriptionActivity : CustomActionBarActivity() {

    private val prescriptionViewModel : PrescriptionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_prescription)

        var groupMemberId = intent.getLongExtra("groupMemberId", 0L)

        prescriptionViewModel.loadPrescriptionData(groupMemberId)

        prescriptionViewModel.prescriptionData.observe(this) { _prescriptionData ->
            if (_prescriptionData != null) {
                generatePrescription(_prescriptionData)
            }
        }
    }
    fun generatePrescription(prescriptionData : List<PrescriptionAndDiseaseNameDTO>){
        val inflater = LayoutInflater.from(this)

        val prescriptionContainer: LinearLayout = findViewById(R.id.prescription_linear)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val prescriptionContent : FrameLayout = inflater.inflate(R.layout.activity_prescription_content, null) as FrameLayout
        val prescriptionRegistrationDate : TextView = prescriptionContent.findViewById(R.id.prescription_registration_date)
        val prescriptionRegistrationHospital : TextView = prescriptionContent.findViewById(R.id.prescription_registration_hospital)
        val prescriptionRegistrationHospitalPhone : TextView = prescriptionContent.findViewById(R.id.prescription_registration_hospital_phone)
        val prescriptionRegistrationDisease : TextView = prescriptionContent.findViewById(R.id.prescription_registration_disease)
        val prescriptionDate : TextView = prescriptionContent.findViewById(R.id.prescription_date)

        //        val prescriptionPillFrame : LinearLayout = prescriptionContent.findViewById(R.id.prescription_pill_data) // 약정보 기입
        //        var prescriptionRegistrationName : TextView = findViewById(R.id.prescription_registration_name)
//        var prescriptionRegistrationBirth : TextView = findViewById(R.id.prescription_registration_birth)

        // 추가할 prescriptionContent 개수 - 처방전 개수
        for (i in prescriptionData.indices) {
            layoutParams.setMargins(0, 0, 0, 16) // 아래쪽에 16dp의 마진
            prescriptionContent.layoutParams = layoutParams

            prescriptionRegistrationDate.text = prescriptionData[i].prescriptionRegistrationDate.toString()
            prescriptionRegistrationHospital.text = prescriptionData[i].hospitalName
            prescriptionRegistrationHospitalPhone.text = prescriptionData[i].hospitalPhone
            prescriptionRegistrationDisease.text = prescriptionData[i].diseaseName
            prescriptionDate.text = prescriptionData.get(i).prescriptionDate.toString()

            //            prescriptionRegistrationName.text = prescriptionData.get(i).gr // 현재 그룹원 이름 필드가 없음
//            prescriptionRegistrationBirth.text = prescriptionData.get(i).date // 생일 필드도 없음

//            val numPillContents = 3 // prescriptionPillContent 개수 - 약 개수
//            for (j in 1..numPillContents) {
//                val prescriptionPillContent : FrameLayout = inflater.inflate(R.layout.activity_prescription_pill_content, null) as FrameLayout
//                prescriptionPillFrame.addView(prescriptionPillContent)
//                layoutParams.setMargins(0, 0, 0, 32) // 아래쪽에 16dp의 마진
//                prescriptionPillContent.layoutParams = layoutParams
//            }
            prescriptionContainer.addView(prescriptionContent)
        }
    }
}
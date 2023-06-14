package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
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

        val groupMemberId = intent.getLongExtra("groupMemberId", 0L)
        val groupMemberName = intent.getStringExtra("groupMemberName")

        val nameText : TextView = findViewById(R.id.prescription_group_member_name)

        nameText.text = groupMemberName

        prescriptionViewModel.loadPrescriptionData(groupMemberId)

        prescriptionViewModel.prescriptionData.observe(this) { _prescriptionData ->
            if (_prescriptionData != null) {
                generatePrescription(_prescriptionData)
            }
        }
    }
    private fun generatePrescription(prescriptionData : List<PrescriptionAndDiseaseNameDTO>){
        val inflater = LayoutInflater.from(this)

        val prescriptionContainer: LinearLayout = findViewById(R.id.prescription_linear)

        prescriptionContainer.removeAllViews()

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // 추가할 prescriptionContent 개수 - 처방전 개수
        for (i in prescriptionData.indices) {
            layoutParams.setMargins(0, 0, 0, 48) // 아래쪽에 48dp의 마진
            val prescriptionContent : FrameLayout = inflater.inflate(R.layout.activity_prescription_content, null) as FrameLayout
            val prescriptionRegistrationDate : TextView = prescriptionContent.findViewById(R.id.prescription_registration_date)
            val prescriptionRegistrationHospital : TextView = prescriptionContent.findViewById(R.id.prescription_registration_hospital)
            val prescriptionRegistrationHospitalPhone : TextView = prescriptionContent.findViewById(R.id.prescription_registration_hospital_phone)
            val prescriptionRegistrationDisease : TextView = prescriptionContent.findViewById(R.id.prescription_registration_disease)
            val prescriptionDate : TextView = prescriptionContent.findViewById(R.id.prescription_date)
            val prescriptionDeleteBtn : ImageButton = prescriptionContent.findViewById(R.id.prescription_delete_btn)
            val prescriptionPillFrame : LinearLayout = prescriptionContent.findViewById(R.id.prescription_pill_data) // 약정보 기입
            prescriptionContent.layoutParams = layoutParams

            prescriptionRegistrationDate.text = "처방전 등록일자 : ${prescriptionData[i].prescriptionRegistrationDate.toString()}"
            prescriptionRegistrationHospital.text = "의료기관 : ${prescriptionData[i].hospitalName}"
            prescriptionRegistrationHospitalPhone.text = "전화번호 : ${prescriptionData[i].hospitalPhone}"
            prescriptionRegistrationDisease.text = "병명 : ${prescriptionData[i].diseaseName}"
            prescriptionDate.text = "처방 일자 : ${prescriptionData[i].prescriptionDate.toString()}"

            // 삭제 버튼
            prescriptionDeleteBtn.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("정말 삭제하시겠습니까?")
                builder.setPositiveButton("예") { dialog, which ->
                    // 삭제 작업 수행
                    prescriptionViewModel.removePrescription(prescriptionData[i])
                }
                builder.setNegativeButton("아니오") { dialog, which ->
                    // 취소 작업 수행
                }
                val dialog = builder.create()
                dialog.show()
            }

            val pillContents = prescriptionData[i].partiallyTakePillDTOList!!
            for (j in pillContents.indices) {
                val prescriptionPillContent : FrameLayout = inflater.inflate(R.layout.activity_prescription_pill_content, null) as FrameLayout
                val pillName : TextView = prescriptionPillContent.findViewById(R.id.prescription_pill_name)
                val pillEatDay : TextView = prescriptionPillContent.findViewById(R.id.prescription_dose_date)
                val pillEatCount : TextView = prescriptionPillContent.findViewById(R.id.prescription_dose_num)

                pillName.text = "약 이름 : ${pillContents[j].pillName}"
                pillEatDay.text = "복약 기간 : ${pillContents[j].takeDay.toString()}"
                pillEatCount.text = "일일 복약 횟수 : ${pillContents[j].takeCount.toString()}"

                prescriptionPillFrame.addView(prescriptionPillContent)
                layoutParams.setMargins(0, 0, 0, 16) // 아래쪽에 16dp의 마진
                prescriptionPillContent.layoutParams = layoutParams
            }
            prescriptionContainer.addView(prescriptionContent)
        }
    }
}
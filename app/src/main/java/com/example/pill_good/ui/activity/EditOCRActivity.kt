package com.example.pill_good.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import com.example.pill_good.R
import com.example.pill_good.data.dto.EditOCRDTO
import com.example.pill_good.data.dto.PillScheduleDTO
import com.example.pill_good.ui.viewmodel.EditOCRViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditOCRActivity : CustomActionBarActivity() {
    private val ocrEditViewModel : EditOCRViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomView(R.layout.activity_edit_ocr)

        val prescriptionEditData = intent.getSerializableExtra("prescriptionData") as? EditOCRDTO

        ocrEditViewModel.setPrescriptionEditData(prescriptionEditData!!)

        ocrEditViewModel.editOCRData.observe(this) { _editOCRData ->
            if (_editOCRData != null) {
                populatePrescription(_editOCRData)
            }
        }

        // 수정 버튼 클릭
        val editButton : Button = findViewById(R.id.prescription_edit_button)
        editButton.setOnClickListener { onEditButtonClicked() }
    }

    // 처방전 수정 데이터 생성
    private fun populatePrescription(prescriptionEditData: EditOCRDTO){
        val editName : EditText = findViewById(R.id.prescription_edit_edittext_name)
        val editHospital : EditText = findViewById(R.id.prescription_edit_edittext_hospital)
        val editHospitalPhone : EditText = findViewById(R.id.prescription_edit_edittext_hospital_phone)
        val editDiseasCode :  EditText = findViewById(R.id.prescription_edit_edittext_disease)

        editName.setText(prescriptionEditData.groupMemberName)
        editHospital.setText(prescriptionEditData.hospitalName)
        editHospitalPhone.setText(prescriptionEditData.phoneNumber)
        editDiseasCode.setText(prescriptionEditData.diseaseCode)

        generatePillItem(prescriptionEditData.pillList)
    }

    // 처방전 수정 데이터 내 약 정보 데이터 생성
    private fun generatePillItem(pillDataList : List<PillScheduleDTO>){
        val inflater = LayoutInflater.from(this)

        val pillContainer: LinearLayout = findViewById(R.id.prescription_edit_pill_container)

//        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val pillContent : FrameLayout = inflater.inflate(R.layout.activity_prescription_edit_ocr_pill_item, null) as FrameLayout
        val pillName : TextView = pillContent.findViewById(R.id.prescription_edit_pill_item_name)
        val pillDay : TextView = pillContent.findViewById(R.id.prescription_edit_pill_item_day)
        val pillEatNum : TextView = pillContent.findViewById(R.id.prescription_edit_pill_item_num)
        val eatWake : CheckBox = pillContent.findViewById(R.id.wake_check)
        val eatMorning : CheckBox = pillContent.findViewById(R.id.morning_check)
        val eatLunch : CheckBox = pillContent.findViewById(R.id.lunch_check)
        val eatDinner : CheckBox = pillContent.findViewById(R.id.dinner_check)
        val eatSleep : CheckBox = pillContent.findViewById(R.id.sleep_check)

        for (i: Int in pillDataList.indices) {
            val pillData = pillDataList[i]

            pillName.text = pillData.pillName
            pillDay.text = pillData.takeDay.toString()
            pillEatNum.text = pillData.takeCount.toString()

            for (j: Int in pillData.takePillTimeList.indices) {
                val pillTime = pillData.takePillTimeList[j]

                // 체크박스 설정
                when (pillTime) {
                    1 -> {
                        eatWake.isChecked = true
                    }
                    2 -> {
                        eatMorning.isChecked = true
                    }
                    3 -> {
                        eatLunch.isChecked = true
                    }
                    4 -> {
                        eatDinner.isChecked = true
                    }
                    5 -> {
                        eatSleep.isChecked = true
                    }
                }
            }
            pillContainer.addView(pillContent)
        }
    }

    // 수정 버튼 클릭 시에 호출되는 함수
    private fun onEditButtonClicked() {
        // 수정된 데이터를 읽어와서 ViewModel에 전달
        val editedPrescriptionData = readEditedDataFromUI()
        ocrEditViewModel.editPillData(editedPrescriptionData)
    }

    // UI에서 변경된 데이터를 읽어오는 함수
    private fun readEditedDataFromUI(): EditOCRDTO {
        val editName: EditText = findViewById(R.id.prescription_edit_edittext_name)
        val editHospital: EditText = findViewById(R.id.prescription_edit_edittext_hospital)
        val editHospitalPhone: EditText = findViewById(R.id.prescription_edit_edittext_hospital_phone)
        val editDiseaseCode: EditText = findViewById(R.id.prescription_edit_edittext_disease)

        val editedName = editName.text.toString()
        val editedHospital = editHospital.text.toString()
        val editedHospitalPhone = editHospitalPhone.text.toString()
        val editedDiseaseCode = editDiseaseCode.text.toString()

        // 수정된 데이터로 PrescriptionData 객체 생성
        val editedPrescriptionData = EditOCRDTO(
            groupMemberName = editedName,
            hospitalName = editedHospital,
            phoneNumber = editedHospitalPhone,
            diseaseCode = editedDiseaseCode,
            pillList = getEditedPillDataList()
        )
        return editedPrescriptionData
    }

    // UI에서 변경된 PillData 리스트를 읽어오는 함수
    private fun getEditedPillDataList(): List<PillScheduleDTO> {
        val editedPillDataList = mutableListOf<PillScheduleDTO>()

        // UI에서 변경된 PillData를 읽어와서 리스트에 추가
        val pillContainer: LinearLayout = findViewById(R.id.prescription_edit_pill_container)
        for (i: Int in 0 until pillContainer.childCount) {
            val pillContent: FrameLayout = pillContainer.getChildAt(i) as FrameLayout
            val pillName: TextView = pillContent.findViewById(R.id.prescription_edit_pill_item_name)
            val pillDay: TextView = pillContent.findViewById(R.id.prescription_edit_pill_item_day)
            val eatWake: CheckBox = pillContent.findViewById(R.id.wake_check)
            val eatMorning: CheckBox = pillContent.findViewById(R.id.morning_check)
            val eatLunch: CheckBox = pillContent.findViewById(R.id.lunch_check)
            val eatDinner: CheckBox = pillContent.findViewById(R.id.dinner_check)
            val eatSleep: CheckBox = pillContent.findViewById(R.id.sleep_check)

            val takePillTimeList =  getSelectedPillTimeList(eatWake, eatMorning, eatLunch, eatDinner, eatSleep)

            val pillData = PillScheduleDTO(
                pillName = pillName.text.toString(),
                takeDay = Integer.parseInt(pillDay.text.toString()),
                takeCount = Integer.parseInt(takePillTimeList.size.toString()),
                takePillTimeList = takePillTimeList
            )

            editedPillDataList.add(pillData)
        }

        return editedPillDataList
    }

    // 체크된 PillTime을 가져오는 함수
    private fun getSelectedPillTimeList(eatWake: CheckBox, eatMorning: CheckBox, eatLunch: CheckBox, eatDinner: CheckBox, eatSleep: CheckBox): List<Int> {
        val selectedPillTimeList = mutableListOf<Int>()

        if (eatWake.isChecked) {
            selectedPillTimeList.add(1)
        }
        if (eatMorning.isChecked) {
            selectedPillTimeList.add(2)
        }
        if (eatLunch.isChecked) {
            selectedPillTimeList.add(3)
        }
        if (eatDinner.isChecked) {
            selectedPillTimeList.add(4)
        }
        if (eatSleep.isChecked) {
            selectedPillTimeList.add(5)
        }
        return selectedPillTimeList
    }
}
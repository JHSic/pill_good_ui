package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.pill_good.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class GroupMemberAddActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_group_member_add)

        val addAliasText: EditText = findViewById(R.id.add_edittext_alias)
        val addPhoneText: EditText = findViewById(R.id.add_edittext_phone)
        val addCalenderButton: ImageButton = findViewById(R.id.add_calendar_button)
        val birthDate: TextView = findViewById(R.id.add_text_birth)
        val editButton: Button = findViewById(R.id.add_button)
        //sharedPreference를 이용한 기기에 선택한 날짜 데이터 저장
        val sharedPreference = getSharedPreferences("CreateProfile", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()

        // 수정 버튼 클릭
        // 현재 그냥 팝업 종료하게 만들어 놨는데 화면을 그냥 종료시킬지 or 다시 업데이트된 화면 불러올지
        editButton.setOnClickListener() {
            val builder = AlertDialog.Builder(this)
            // 없거나 중복일 경우 -> 중복 체크 조건 추가
            if(addAliasText.text.toString().trim().isEmpty()){
                builder.setMessage("별칭은 필수입니다.")

            }
            else{
                // 수정 완료 되고 뷰 종료 or 이동하는 로직
                builder.setMessage("새로운 그룹원이 추가되었습니다..")

            }
            // 완료시 화면 이동하는 구조가 무조건 되어야하므로 다시 조건문 안으로 각각 들어가야할듯
            builder.setPositiveButton("확인") { dialog, which ->
                // 그냥 팝업만 닫음.
            }
            val dialog = builder.create()
            dialog.show()
        }

        // 달력 버튼 클릭
        addCalenderButton.setOnClickListener() {
            //calendar Constraint Builder 선택할수있는 날짜 구간설정
            val calendarConstraintBuilder = CalendarConstraints.Builder()
            //오늘 이후만 선택가능하게 하는 코드
//            calendarConstraintBuilder.setValidator(DateValidatorPointForward.now())
            //오늘 이전만 선택가능하게 하는 코드
            calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now())


            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Calendar")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())

                //위에서 만든 calendarConstraint을 builder에 설정.
                .setCalendarConstraints(calendarConstraintBuilder.build());

            val datePicker = builder.build()
            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.time = Date(it)
                val calendarMilli = calendar.timeInMillis
                birthDate.text =
                    "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(
                        Calendar.DAY_OF_MONTH)}"

                //sharedPreference${calendar.get(Calendar.MONTH) + 1}
                editor.putLong("Die_Millis", calendarMilli)
                editor.apply()
            }
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
    }
}
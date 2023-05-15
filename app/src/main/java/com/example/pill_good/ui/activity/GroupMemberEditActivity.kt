package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
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

class GroupMemberEditActivity : CustomActionBarActivity() {

    private var datePickerDialog: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomView(R.layout.activity_group_member_edit)

        val editAliasText: EditText = findViewById(R.id.edittext_alias)
        val editPhoneText: EditText = findViewById(R.id.edittext_phone)
        val editCalenderButton: ImageButton = findViewById(R.id.edit_birth_Button)
        val birthDate: TextView = findViewById(R.id.text_birth)
        val editButton: Button = findViewById(R.id.edit_button)
        //sharedPreference를 이용한 기기에 선택한 날짜 데이터 저장
        val sharedPreference = getSharedPreferences("CreateProfile", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()

        // 수정 버튼 클릭
        editButton.setOnClickListener() {
            if(editAliasText.text.toString().trim().isEmpty()){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("별칭은 필수입니다.")
                builder.setPositiveButton("확인") { dialog, which ->
                    // 그냥 팝업만 닫음
                }
                val dialog = builder.create()
                dialog.show()
            }
            else{
                // 수정 완료 되고 뷰 종료 or 이동하는 로직
            }

        }

        // 달력 버튼 클릭
        editCalenderButton.setOnClickListener() {
            //calendar Constraint Builder 선택할수있는 날짜 구간설정
            val calendarConstraintBuilder = CalendarConstraints.Builder()
            //오늘 이후만 선택가능하게 하는 코드
//            calendarConstraintBuilder.setValidator(DateValidatorPointForward.now())
            //오늘 이전만 선택가능하게 하는 코드
            calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now())


            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Calendar")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())

                //위에서 만든 calendarConstraint을 builder에 설정해줍니다.
                .setCalendarConstraints(calendarConstraintBuilder.build());

            val datePicker = builder.build()
            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.time = Date(it)
                val calendarMilli = calendar.timeInMillis
                birthDate.text =
                    "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}"

                //sharedPreference${calendar.get(Calendar.MONTH) + 1}
                editor.putLong("Die_Millis", calendarMilli)
                editor.apply()
            }
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
    }

}

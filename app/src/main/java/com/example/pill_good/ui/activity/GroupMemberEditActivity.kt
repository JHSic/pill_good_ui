package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import com.example.pill_good.R
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.ui.viewmodel.GroupViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class GroupMemberEditActivity : CustomActionBarActivity() {
    private val groupViewModel: GroupViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_group_member_edit)

        val groupMemberInformation = intent.getSerializableExtra("groupMemberInformation") as GroupMemberAndUserIndexDTO

        val editAliasText: EditText = findViewById(R.id.edittext_alias)
        val editPhoneText: EditText = findViewById(R.id.edittext_phone)
        val birthDate: TextView = findViewById(R.id.text_birth)

        editAliasText.setText(groupMemberInformation?.groupMemberName)
        editPhoneText.setText(groupMemberInformation?.groupMemberPhone)
        birthDate.text = groupMemberInformation?.groupMemberBirth.toString()

        val editCalenderButton: ImageButton = findViewById(R.id.edit_birth_Button)
        val editButton: Button = findViewById(R.id.edit_button)
        //sharedPreference를 이용한 기기에 선택한 날짜 데이터 저장
        val sharedPreference = getSharedPreferences("CreateProfile", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()

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
                //위에서 만든 calendarConstraint을 builder에 설정
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

        // 수정 버튼 클릭
        editButton.setOnClickListener() {
            val updateAlias = editAliasText.text.toString().trim()

            if(updateAlias.isEmpty()){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("별칭은 필수입니다.")
                builder.setPositiveButton("확인") { dialog, which ->
                    // 그냥 팝업만 닫음
                }
                val dialog = builder.create()
                dialog.show()
            }
            else{
                val updatePhone = editPhoneText.text.toString().trim()
                val updateBirth = convertToDate(birthDate.text.toString())

                // 모두 일치하면 view만 종료
                if(groupMemberInformation?.groupMemberName != updateAlias
                    || groupMemberInformation.groupMemberPhone != updatePhone
                    || groupMemberInformation.groupMemberBirth != updateBirth){
                    val updatedGroupMember = groupMemberInformation?.copy(
                        groupMemberName = updateAlias,
                        groupMemberPhone = updatePhone,
                        groupMemberBirth = updateBirth
                    )
                    println("업데이트된 유저의 이름 " + updateAlias)
                    groupViewModel.editGroupMember(updatedGroupMember!!)
                    Toast.makeText(applicationContext, "수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                }
                finish()
            }
        }
    }

    private fun convertToDate(birthdate: String): LocalDate {
        // 날짜 형식 지정
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        // birthdate 문자열을 LocalDate로 변환
        return LocalDate.parse(birthdate.trim(), formatter)
    }

}

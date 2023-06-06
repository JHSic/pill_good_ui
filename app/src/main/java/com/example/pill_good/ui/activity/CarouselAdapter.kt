package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_good.R
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.model.CarouselItem
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.Date

class CarouselAdapter(private val carouselItems: List<CarouselItem>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    var resultBirthEditText: String = ""
    var resultGroupMember: GroupMemberAndUserIndexDTO = GroupMemberAndUserIndexDTO()

    private var groupMemberList: ArrayList<GroupMemberAndUserIndexDTO> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(viewType, parent, false)
        return CarouselViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val carouselItem = carouselItems[position]
        // 아이템에 따라 처리할 로직 작성
        when (holder.itemViewType) {
            R.layout.carousel_image_item -> {
                // 이미지 아이템 처리
                val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)

                // 이미지 URI를 사용하여 이미지 로드 등의 작업 수행
                imageView.setImageURI(carouselItem.imageUri)
            }
            R.layout.carousel_linear_item -> {
                // LinearLayout 아이템 처리
                val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView2)

                // 이미지 URI를 사용하여 이미지 로드 등의 작업 수행
                imageView.setImageURI(carouselItem.imageUri)

                // 필요한 로직 작성
                val birthEditText = holder.itemView.findViewById<TextView>(R.id.add_text_take_pill_date)
                birthEditText.isFocusableInTouchMode = false
                birthEditText.hint = "달력 버튼을 눌러주세요."

                val groupEditText = holder.itemView.findViewById<EditText>(R.id.add_text_groupmember)
                birthEditText.isFocusableInTouchMode = false
                groupEditText.hint = "그룹원 버튼을 눌러주세요."

                //sharedPreference를 이용한 기기에 선택한 날짜 데이터 저장
                val sharedPreference = activity.getSharedPreferences("CreateProfile", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreference.edit()

                // 달력 버튼 클릭
                val addCalenderButton = holder.itemView.findViewById<ImageButton>(R.id.add_calendar_button3)
                addCalenderButton.setOnClickListener {
                    //calendar Constraint Builder 선택할수있는 날짜 구간설정
                    val calendarConstraintBuilder = CalendarConstraints.Builder()
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
                        birthEditText.text =
                            "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(
                                Calendar.DAY_OF_MONTH)}"

                        resultBirthEditText = birthEditText.text.toString()

                        //sharedPreference${calendar.get(Calendar.MONTH) + 1}
                        editor.putLong("Die_Millis", calendarMilli)
                        editor.apply()
                    }
                    datePicker.show(activity.supportFragmentManager, datePicker.toString())
                }

                val addGroupMemberButton = holder.itemView.findViewById<ImageButton>(R.id.add_group_button4)
                addGroupMemberButton.setOnClickListener {
                    val alertBuilder = AlertDialog.Builder(activity)
                    alertBuilder.setTitle("그룹원 선택")
                    alertBuilder.setItems(groupMemberList.map { it -> it.groupMemberName }.toTypedArray()) { dialog: DialogInterface, which: Int ->
                        resultGroupMember = groupMemberList[which]
                        groupEditText.text = Editable.Factory.getInstance().newEditable(groupMemberList[which].groupMemberName)

                        dialog.dismiss()
                    }
                    alertBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
                        dialog.dismiss()
                    }
                    val dialog = alertBuilder.create()
                    dialog.show()
                }
            }
        }
    }

    override fun getItemCount(): Int = carouselItems.size

    override fun getItemViewType(position: Int): Int = carouselItems[position].layoutResId

    fun setGroupMemberList(groupMemberList: ArrayList<GroupMemberAndUserIndexDTO>) {
        this.groupMemberList = groupMemberList
    }

    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

package com.example.pill_good.ui.activity

import android.content.Context
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_good.R
import com.example.pill_good.data.model.CarouselItem
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.Date

class CarouselAdapter(private val carouselItems: List<CarouselItem>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    var resultBirthEditText: String = ""
    var resultGroupMember: String = ""

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
                birthEditText.hint = "달력을 눌러주세요."

                val groupEditText = holder.itemView.findViewById<EditText>(R.id.add_text_groupmember)
                groupEditText.hint = "그룹원 별칭을 입력해주세요."

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

                // TextWatcher 객체 생성
                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // 텍스트 변경 전에 호출되는 메서드
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // 텍스트가 변경될 때 호출되는 메서드
                        val newText = s.toString()
                        // 변경된 텍스트를 이용하여 원하는 동작 수행
                        // 예: 데이터 변경 등
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // 텍스트 변경 후에 호출되는 메서드
                        resultGroupMember = s.toString()
                    }
                }

                // EditText에 TextWatcher 등록
                groupEditText.addTextChangedListener(textWatcher)
            }
        }
    }

    override fun getItemCount(): Int = carouselItems.size

    override fun getItemViewType(position: Int): Int = carouselItems[position].layoutResId



    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

package com.example.pill_good.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.example.pill_good.R
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.ui.viewmodel.GroupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupActivity : CustomActionBarActivity() {
    private val groupViewModel: GroupViewModel by viewModel()
    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomView(R.layout.activity_group)

        val groupMemberList = intent.getSerializableExtra("groupMemberList") as? ArrayList<GroupMemberAndUserIndexDTO>
        groupViewModel.setGroupDataByMainData(groupMemberList!!)
        userId = groupMemberList.get(0).userIndex
        val groupButton: ImageButton = findViewById(R.id.group_button)
        groupButton.alpha = 1f
        groupButton.isEnabled = false

        groupViewModel.groupData.observe(this) { _groupData ->
            if (_groupData != null) {
                populateViews(_groupData)
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        groupViewModel.groupData.observe(this) { _groupData ->
//            if (_groupData != null) {
//                populateViews(_groupData)
//            }
//        }
//    }


    private fun populateViews(groupData : List<GroupMemberAndUserIndexDTO>) {
        val linearLayout = findViewById<LinearLayout>(R.id.group_linear)
        linearLayout.removeAllViews()

        var n = groupViewModel.groupData.value!!.size
        val rows = (n + 2) / 2 // 카드를 출력할 줄 수
        var lastRowNumOfCards = n % 2 // 마지막 줄의 카드 수
        if (lastRowNumOfCards == 0 && n > 0) { // 그룹원 수가 짝수인 경우
            lastRowNumOfCards = 2
        }
        for (i: Int in 1..rows) {
            var cardRow = createCardRow()

            val layoutParams1 = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1F
            )
            layoutParams1.marginStart = 64 // 오른쪽 마진
            layoutParams1.marginEnd = 32 // 왼쪽 마진
            val layoutParams2 = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1F
            )
            layoutParams2.marginStart = 32
            layoutParams2.marginEnd = 64

            // 왼쪽 카드
            if(i * 2 - 1 <= n){
                createCard(layoutParams1, cardRow, i, 0, groupData.get(i * 2 - 2))
            }
            else{
                createCard(layoutParams1, cardRow, i, 1, null)
            }

            // 오른쪽 카드
            if (i * 2 <= n) {
                createCard(layoutParams2, cardRow, i, 0, groupData.get(i * 2 - 1))
            } else if (lastRowNumOfCards == 1) { // 마지막 줄이 1개 카드인 경우
                createCard(layoutParams2, cardRow, i, 1, null) // 그룹원 추가 버튼 생성
            }
            linearLayout.addView(cardRow)
        }

    }

    private fun createCard(layoutParams: LayoutParams, cardRow: LinearLayout, cardNum: Int, createType: Int, groupMemberData: GroupMemberAndUserIndexDTO?){
        val card = CardView(this)
        card.layoutParams = layoutParams
        card.radius = 36F // 둥근 정도
        card.setContentPadding(25,25,25,25)
//        card.setCardBackgroundColor(Color.WHITE)
        card.setBackgroundResource(R.drawable.prescription_card_border)
        card.cardElevation = 12F  // 그림자
        card.maxCardElevation = 20F  // 눌렀을 때 그림자
        card.clipToPadding = false  // 패딩 영역 밖까지 그림자 표시
        if(createType == 0){
            card.addView(generateCardView(cardNum, layoutParams, groupMemberData!!)) // 그룹원 이름을 주는 메소드로 변경
        }
        else if(createType == 1){
            if(layoutParams.rightMargin == 32){
                layoutParams.marginEnd = 64
            }
            card.addView(generateAddCardView()) // 그룹원 추가 카드뷰 생성
        }
        cardRow.addView(card)
    }

    // 카드뷰 생성을 위한 줄 생성 - LinearLayout을 수평으로 생성하여 2개의 카드뷰 배치
    private fun createCardRow() : LinearLayout{
        val cardRow = LinearLayout(this)
        cardRow.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 64
        cardRow.layoutParams = layoutParams
        return cardRow
    }

    // 그룹원을 추가하는 카드뷰 레이아웃 생성.
    // 홀수인 경우에는 그룹원 카드뷰와 같은 크기의 뷰 생성
    // 짝수인 경우에는 그룹원 카드뷰 밑 한 줄을 가득 채우는 카드뷰 생성
    private fun generateAddCardView() : LinearLayout{
        val cardLinearLayout = LinearLayout(this)
//        cardLinearLayout.orientation = LinearLayout.VERTICAL

        val groupMemberAddIcon = ImageView(this)
        groupMemberAddIcon.setImageResource(R.drawable.group_member_add_icon)
        groupMemberAddIcon.setBackgroundColor(Color.WHITE)
        groupMemberAddIcon.setPadding(75,75,75,75)
        val iconLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT// 아이콘 이미지 크기 지정
        )
        iconLayoutParams.gravity = Gravity.CENTER
        cardLinearLayout.addView(groupMemberAddIcon, iconLayoutParams)

        groupMemberAddIcon.setOnClickListener{
            // 그룹원 추가 페이지로 이동
            val intent = Intent(this,GroupMemberAddActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        return cardLinearLayout
    }

    // 카드뷰 내용 생성하는 메소드
    private fun generateCardView(cardNum: Int, layoutParams: LayoutParams, groupMemberData: GroupMemberAndUserIndexDTO): LinearLayout {
        val font = ResourcesCompat.getFont(this, R.font.pt_sans_regular) // 폰트를 가져옴
        val cardLinearLayout = LinearLayout(this)
        cardLinearLayout.orientation = LinearLayout.VERTICAL

        val groupMemberIcon = ImageView(this)
        groupMemberIcon.setImageResource(R.drawable.group_member_icon)

        val iconLayoutParams = LinearLayout.LayoutParams(
            200, 200 // 아이콘 이미지 크기 지정
        )
        iconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL

        val groupMemberName = TextView(this)
        // 내부에서 처리 했으므로 상관 없을 예정
        // 그러나 위에 메소드 분해하면 중복 코드가 생겨서 nullable하게 받기에 null체크
        if (groupMemberData != null) {
            groupMemberName.text = groupMemberData.groupMemberName
        }
        groupMemberName.textSize = 17f
        groupMemberName.typeface = font
        groupMemberName.gravity = Gravity.CENTER_HORIZONTAL
        groupMemberName.setTextColor(Color.BLACK)

        cardLinearLayout.addView(groupMemberIcon, iconLayoutParams)
        cardLinearLayout.addView(groupMemberName)

        // 첫번째 그룹원은 유저이므로 수정, 삭제, 메시지 버튼 미생성
        if(cardNum == 1 && layoutParams.leftMargin == 64){
            cardLinearLayout.setOnClickListener {
                val intent = Intent(this,PrescriptionActivity::class.java)
                startActivity(intent)
            }
            return cardLinearLayout
        }

        val buttonLayout = LinearLayout(this)
        buttonLayout.orientation = LinearLayout.HORIZONTAL

        // 수정 버튼
        val groupMemberEditButton = ImageButton(this)
        groupMemberEditButton.setImageResource(R.drawable.group_member_edit)
        groupMemberEditButton.setBackgroundColor(Color.WHITE)
        buttonLayout.addView(groupMemberEditButton)
        groupMemberEditButton.setOnClickListener {
            // 수정 페이지로 이동
            val intent = Intent(this,GroupMemberEditActivity::class.java)
            intent.putExtra("groupMemberInformation", groupMemberData)
            startActivity(intent)
        }

        // 삭제 버튼
        val groupMemberDeleteButton = ImageButton(this)
        groupMemberDeleteButton.setImageResource(R.drawable.group_member_delete)
        groupMemberDeleteButton.setBackgroundColor(Color.WHITE)
        buttonLayout.addView(groupMemberDeleteButton)
        groupMemberDeleteButton.setOnClickListener {
            // 삭제
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정말 삭제하시겠습니까?")
            builder.setPositiveButton("예") { dialog, which ->
                // 삭제 작업 수행
                groupViewModel.removeGroupMember(groupMemberData)
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                // 취소 작업 수행
            }
            val dialog = builder.create()
            dialog.show()
        }

        // 메시지 버튼
        val imageButton = ImageButton(this)
        imageButton.setImageResource(R.drawable.group_member_message) // 기본 아이콘 설정
        imageButton.setBackgroundColor(Color.WHITE)

        // 생성 시 메시지 전송여부 설정
        if(groupMemberData.messageCheck == true){
            imageButton.setImageResource(R.drawable.group_member_message)
        }
        else{
            imageButton.setImageResource(R.drawable.group_member_no_message)
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageButton.layoutParams = layoutParams

        // 토글 상태 변경 시 메시지 전송여부 변경
        imageButton.setOnClickListener {
            if (groupMemberData.messageCheck == false) {
                imageButton.setImageResource(R.drawable.group_member_message)
                val updateGroupMember = groupMemberData.copy(
                    messageCheck = true
                )
                println(groupMemberData.groupMemberName + "의 메시지 전송여부 변경 기존 : " + groupMemberData.messageCheck + "변경" + updateGroupMember.messageCheck )
                groupViewModel.editGroupMember(updateGroupMember)
            } else if(groupMemberData.messageCheck == true) {
                imageButton.setImageResource(R.drawable.group_member_no_message)
                val updateGroupMember = groupMemberData.copy(
                    messageCheck = false
                )
                groupViewModel.editGroupMember(updateGroupMember)
                println(groupMemberData.groupMemberName + "의 메시지 전송여부 변경 기존 : " + groupMemberData.messageCheck + "변경" + updateGroupMember.messageCheck )
            }

            imageButton.isSelected = !imageButton.isSelected

        }
        buttonLayout.addView(imageButton)

        buttonLayout.gravity = Gravity.CENTER_HORIZONTAL
        // cardLinearLayout에 buttonLayout을 추가
        cardLinearLayout.addView(buttonLayout)

        cardLinearLayout.setOnClickListener {
            val intent = Intent(this,PrescriptionActivity::class.java)
            intent.putExtra("groupMemberId", groupMemberData.groupMemberIndex)
            startActivity(intent)
        }

        return cardLinearLayout
    }
}


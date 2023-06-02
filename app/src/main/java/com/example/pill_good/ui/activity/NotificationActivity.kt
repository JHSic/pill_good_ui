package com.example.pill_good.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.example.pill_good.R
import com.example.pill_good.data.dto.NotificationDTO
import com.example.pill_good.ui.viewmodel.NotificationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.time.LocalDateTime

class NotificationActivity : CustomActionBarActivity() {
    private val notificationViewModel : NotificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_notification)

        var userId = intent.getLongExtra("userId", 0L)

        notificationViewModel.loadNotificationData() // userId 넣어줘야함

        notificationViewModel.notificationData.observe(this) { _notificationData ->
            if (_notificationData != null) {
                populateNotification(_notificationData)
            }
        }
    }

    private fun populateNotification(notificationData : List<NotificationDTO>){
        val linearLayout = findViewById<LinearLayout>(R.id.notification_linear)
        // 알림 개수 만큼 생성되게 설정
        for(i: Int in notificationData.indices) {
            // Initialize a new LayoutParams instance, CardView width and height
            val layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // CardView width
                ViewGroup.LayoutParams.WRAP_CONTENT // CardView height
            )

            layoutParams.setMargins(64,24,64,28)

            // 카드뷰 생성
            val notificationCard = CardView(this)

            // layout params 설정
            notificationCard.layoutParams = layoutParams

            // 둥근 정도 설정
            notificationCard.radius = 40F

            // 패딩 설정
            notificationCard.setContentPadding(25,25,25,25)

            // 배경색 설정
//            notificationCard.setCardBackgroundColor(Color.WHITE)
            notificationCard.setBackgroundResource(R.drawable.prescription_card_border)

            // 그림자
            notificationCard.cardElevation = 8F

            // 그림자 최대치
            notificationCard.maxCardElevation = 12F

            // 카드뷰 내용 생성
            notificationCard.addView(generateCardView(notificationData[i]))

            // linearLayout에 카드뷰 추가
            linearLayout.addView(notificationCard)
        }
    }

    // Custom method to generate an image view
    private fun generateCardView(notificationDTO : NotificationDTO): LinearLayout {
        val font = ResourcesCompat.getFont(this, R.font.pt_sans_regular) // 폰트를 가져옴
        val cardLinearLayout = LinearLayout(this)
        cardLinearLayout.orientation = LinearLayout.VERTICAL

        val notificationContent = TextView(this)
        notificationContent.text = notificationDTO.notificationContent
        notificationContent.textSize = 20f
        notificationContent.setTextColor(Color.BLACK)
        notificationContent.typeface = font

        val notificationTime = TextView(this)
        val timeDifference = Duration.between(notificationDTO.notificationTime, LocalDateTime.now())

        val timeText = when {
            timeDifference.toMinutes() < 60 -> "${timeDifference.toMinutes()}분전"
            timeDifference.toHours() < 24 -> "${timeDifference.toHours()}시간전"
            timeDifference.toDays() == 1L -> "1일전"
            timeDifference.toDays() < 30 -> "${timeDifference.toDays()}일전"
            else -> "30일 이상 전"
        }
        notificationTime.text = timeText
        notificationTime.textSize = 15f
        notificationTime.setTextColor(Color.BLACK)
        notificationTime.typeface = font

        cardLinearLayout.addView(notificationContent)
        cardLinearLayout.addView(notificationTime)

        return cardLinearLayout
    }
}
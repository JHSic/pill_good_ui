package com.example.pill_good.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.pill_good.R

class NotificationActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_notification)
        val linearLayout = findViewById<LinearLayout>(R.id.notification_linear)

        // 알림 개수 만큼 생성되게 설정
        for(i: Int in 1..5) {
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
            notificationCard.setCardBackgroundColor(Color.WHITE)

            // 그림자
            notificationCard.cardElevation = 8F

            // 그림자 최대치
            notificationCard.maxCardElevation = 12F

            // Add LinearLayout to the CardView
            notificationCard.addView(generateCardView())

            // Finally, add the CardView in root layout
            linearLayout.addView(notificationCard)
        }
    }

    // Custom method to generate an image view
    private fun generateCardView(): LinearLayout {

        val cardLinearLayout = LinearLayout(this)
        cardLinearLayout.orientation = LinearLayout.VERTICAL

        val notificationContent = TextView(this)
        notificationContent.text = "아침약 알림입니다."
        notificationContent.textSize = 20f
        notificationContent.setTextColor(Color.BLACK)

        val notificationTime = TextView(this)
        notificationTime.text = "20분전"
        notificationTime.textSize = 15f
        notificationTime.setTextColor(Color.BLACK)

        cardLinearLayout.addView(notificationContent)
        cardLinearLayout.addView(notificationTime)

        return cardLinearLayout
    }
}
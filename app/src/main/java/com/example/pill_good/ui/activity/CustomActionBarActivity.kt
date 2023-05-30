package com.example.pill_good.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.example.pill_good.R
import com.google.firebase.auth.FirebaseUser

open class CustomActionBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_action_bar)

        // 액션바 삭제돼서 툴바로 통용돼서 사용. 커스텀 툴바를 사용하므로 디폴트 툴바 비활성화 후 제작한 커스텀 액션바로 대체
        val toolbar: Toolbar = findViewById(R.id.custom_action_bar)
        setSupportActionBar(toolbar)

        val logoText: TextView = toolbar.findViewById(R.id.logo)
        val menuButton: ImageButton = toolbar.findViewById(R.id.menu_button)
        val notificationButton: ImageButton = toolbar.findViewById(R.id.notification_button)
        val cameraButton: ImageButton = findViewById(R.id.camera_button)
        val calendarButton: ImageButton = findViewById(R.id.calendar_button)
        val groupButton: ImageButton = findViewById(R.id.group_button)

        /* 로고 클릭 설정
            클릭 시 메인 화면으로 이동
         */
        logoText.setOnClickListener {
            // 로고 클릭 시 동작하는 코드 작성 -> 사실상 메인
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        /* 메뉴 버튼 설정
            클릭 시 메뉴 팝업 출력
         */
        menuButton.setOnClickListener {
            showMenu(menuButton)
        }

        /* 알림 버튼 설정
            클릭 시 알림 내역 화면으로 이동
         */
        notificationButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0);
        }

        // 카메라 버튼 설정
        cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0);
        }

        // 캘린더 버튼 설정 - 메인 액티비티
        calendarButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0);
        }

        // 그룹 버튼 설정
        groupButton.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0);
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        overridePendingTransition(0, 0);
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0);
    }

    /*
        메뉴 클릭 시 팝업 옵션 선택 창
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pill_search -> {
                // 약 검색 메뉴 아이템 클릭 시 동작하는 코드 작성
                val intent = Intent(this, SearchPillActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return true
            }

            R.id.settings -> {
                // 설정 메뉴 아이템 클릭 시 동작하는 코드 작성
//                toActivity("SettingActivity")
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // 이유는 모르겠는데 이거 안하면 아이콘이 안나옴
    @SuppressLint("RestrictedApi")
    private fun showMenu(anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.custom_menu, popup.menu)

        // 테두리 적용 코드
        val menuHelper = MenuPopupHelper(this, popup.menu as MenuBuilder, anchorView)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()

        popup.setOnMenuItemClickListener { item ->
            onOptionsItemSelected(item)
        }
    }

    /*
        현재 구조는 상단, 하단 액션바를 구현 후 나머지 클래스가 상속받아 사용하는 구조
        그렇기에 코드 간략화를 위해 하단 액션바를 상단 액션바와 같이 구현해서 상속 받아 사용
        빈 공간에는 프래그먼트를 선언하여 해당 클래스를 상속받는 클래스들의 뷰를 띄울 수 있게 사용
    */
    protected fun addCustomView(layoutResID: Int) {
        val customContent = findViewById<FrameLayout>(R.id.custom_action_bar_container)
        layoutInflater.inflate(layoutResID, customContent, true)
    }

}
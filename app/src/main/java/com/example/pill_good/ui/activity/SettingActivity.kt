package com.example.pill_good.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pill_good.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : CustomActionBarActivity() {
    private val SMS_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 1002
    private val APP_SETTINGS_REQUEST_CODE = 2001

    private lateinit var messageSwitch: Switch
    private lateinit var cameraSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 빈 공간 위에 추가할 뷰
        addCustomView(R.layout.activity_settings)

        messageSwitch = findViewById(R.id.message_Authorization)
        cameraSwitch = findViewById(R.id.camera_Authorization)

        messageSwitch.isEnabled = checkSmsPermission()
        cameraSwitch.isEnabled = checkCameraPermission()

        messageSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 스위치가 켜진 경우
                if (!checkSmsPermission()) {
                    // 카메라 권한을 요청
                    requestSmsPermission()
                }
            } else {
                // 스위치가 꺼진 경우
                // 권한 해제 또는 필요한 처리 수행
                openAppSettings() // 앱 설정 화면으로 이동
            }
        }

        cameraSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 스위치가 켜진 경우
                if (!checkCameraPermission()) {
                    // 카메라 권한을 요청
                    requestCameraPermission()
                }
            } else {
                // 스위치가 꺼진 경우
                // 권한 해제 또는 필요한 처리 수행
                openAppSettings() // 앱 설정 화면으로 이동
            }
        }

        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener{
           signOut()
            var logoutIntent = Intent (this, LoginActivity::class.java)
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(logoutIntent)
            finish() // Activity 종료
        }
    }

    private val appSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 앱 설정 화면에서 돌아온 경우 처리
            // 권한 상태 다시 확인
            messageSwitch.isChecked = checkSmsPermission()
            cameraSwitch.isChecked = checkCameraPermission()
        }
    }

    private fun signOut() { // 로그아웃
        FirebaseAuth.getInstance().signOut()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_ID))
            .requestEmail()
            .build()

        // Firebase sign out
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess()
        }
    }

    // SMS 권한 확인
    private fun checkSmsPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 확인
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // SMS 권한 요청
    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
    }

    // 카메라 권한 요청
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    // 권한 변경을 위한 디바이스-앱 설정 이동
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        appSettingsLauncher.launch(intent)
    }

    // 권한 시스템 창 선택에 따른 스위치 설정
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            SMS_PERMISSION_REQUEST_CODE -> {
                messageSwitch.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                cameraSwitch.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}
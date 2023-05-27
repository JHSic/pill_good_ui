package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import com.example.pill_good.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 빈 공간 위에 추가할 뷰
        addCustomView(R.layout.activity_settings)

        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener{
           signOut()
            var logoutIntent = Intent (this, LoginActivity::class.java)
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(logoutIntent)
            finish() // Activity 종료
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
}
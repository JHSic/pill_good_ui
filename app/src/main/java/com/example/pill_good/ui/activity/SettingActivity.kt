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
            startActivity(logoutIntent)
        }
    }

    private fun signOut() { // 로그아웃
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_ID))
            .requestEmail()
            .build()
        var googleSignInClient : GoogleSignInClient?= null

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        var firebaseAuth = FirebaseAuth.getInstance()
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }
    }
}
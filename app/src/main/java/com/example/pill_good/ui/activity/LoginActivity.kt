package com.example.pill_good.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.pill_good.R
import com.example.pill_good.data.dto.LoginDTO
import com.example.pill_good.data.dto.UserDTO
import com.example.pill_good.databinding.ActivityLoginBinding
import com.example.pill_good.repository.LoginRepositoryImpl
import com.example.pill_good.repository.UserRepositoryImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.logger.Logger
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private val loginRepositoryImpl: LoginRepositoryImpl by inject()

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityLoginBinding

    //private const val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.googleLoginButton.setOnClickListener { signIn() }

        //Google 로그인 옵션 구성. requestIdToken 및 Email 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_ID))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()
    }

    // onStart. 유저가 앱에 이미 구글 로그인을 했는지 확인
    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            var fcmToken: String = ""
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 토큰을 얻어옵니다.
                        fcmToken = task.result
                        Log.d("FCM Token 1", fcmToken)
                        // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
                        toMainActivity(currentUser, fcmToken)
                    } else {
                        Log.w("FCM Token", "Fetching token failed", task.exception)
                    }
                }

        }
    } // onStart End


    // onActivityResult
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e("LoginActivity", "Google sign in failed: ${e.statusCode}", e)
            }
        }
    }


    // firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)

        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val fcmToken = task.result
                                Log.d("FCM Token", fcmToken)
                                toMainActivity(firebaseAuth?.currentUser, fcmToken)
                            } else {
                                Log.w("FCM Token 2", "Fetching token failed", task.exception)
                            }
                        }
                } else {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
                }
            }
    }



    // toMainActivity
    fun toMainActivity(user: FirebaseUser?, fcmToken : String) {
        if (user != null) { // MainActivity 로 이동
            val intent = Intent(this, MainActivity::class.java)
            val loginDTO = LoginDTO(
                userEmail = user.email,
                userToken = fcmToken
            )
            runBlocking {
                try {
                    val loginUserInfo = loginRepositoryImpl.login(loginDTO)
                    intent.putExtra("userId", loginUserInfo?.userIndex)
                    intent.putExtra("userEmail", loginUserInfo?.userEmail)
                    intent.putExtra("userFcmToken", loginUserInfo?.userFcmToken)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
                } catch (e: Exception) {
                    // 예외 처리 로직 추가
                    Log.e("Login Error", "Failed to login", e)
                    // 예외 발생 시 사용자에게 알림 등의 동작 수행

                    // 다이얼로그 띄우기
                    val alertDialog = AlertDialog.Builder(this@LoginActivity)
                        .setTitle("앱 종료")
                        .setMessage("로그인에 실패했습니다. 네트워크 연결을 확인 후 다시 실행해주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                            finishAffinity() // 앱 종료
                        }
                        .setCancelable(false) // 취소 버튼 비활성화
                        .create()
                    alertDialog.show()
                }
            }
        }
    }
//            // For Test
//            val loginUserInfo = UserDTO(
//                userIndex = 1L,
//                userEmail = user.email,
//                userFcmToken = fcmToken
//            )
//
//            intent.putExtra("userId", loginUserInfo?.userIndex)
//            intent.putExtra("userEmail", loginUserInfo?.userEmail)
//            intent.putExtra("userFcmToken", loginUserInfo?.userFcmToken)
//            startActivity(intent)
//            finish()
//            overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
    // toMainActivity End

    // signIn
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // signIn End

    override fun onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 동작할 내용을 정의합니다.
        // 로그아웃 화면에서는 아무 동작도 수행하지 않도록 합니다.
    }
}
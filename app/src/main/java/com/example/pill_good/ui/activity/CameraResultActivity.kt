package com.example.pill_good.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.pill_good.R
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.UserDTO
import com.example.pill_good.data.model.CarouselItem
import com.example.pill_good.databinding.ActivityResultCameraBinding
import com.example.pill_good.ui.viewmodel.CameraResultViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CameraResultActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityResultCameraBinding
    private lateinit var carouselAdapter: CarouselAdapter

    private val cameraResultViewModel: CameraResultViewModel by viewModel()

    private val RESULT_WIDTH_WEIGHT = 400

    private var groupMemberList: ArrayList<GroupMemberAndUserIndexDTO> = arrayListOf()

    private var userInfo: UserDTO = UserDTO()

    private var imgFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityResultCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val imageUri = Uri.parse(intent.getStringExtra("savedUri"))
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val rectangleLeft = intent.getIntExtra("rectangleLeft", 0)
        val rectangleTop = intent.getIntExtra("rectangleTop", 0)
        val rectangleRight = intent.getIntExtra("rectangleRight", 0)
        val rectangleBottom = intent.getIntExtra("rectangleBottom", 0)

        groupMemberList = intent.getSerializableExtra("groupMemberList") as ArrayList<GroupMemberAndUserIndexDTO>
        userInfo = intent.getSerializableExtra("userInfo") as UserDTO

        // 이미지 회전하기
        val rotatedBitmap = rotateBitmap(bitmap, 90f)

        // 이미지와 디바이스 간의 카메라 가이드라인 좌표 매핑하기
        val cropRect = getMappedRectArea(rotatedBitmap, rectangleLeft, rectangleTop, rectangleRight, rectangleBottom)

        // 영역을 바탕으로 비트맵을 크롭
        val croppedBitmap = Bitmap.createBitmap(
            rotatedBitmap,
            cropRect.left,
            cropRect.top,
            cropRect.width(),
            cropRect.height()
        )

        // 기존 사진 덮어쓰기
        val outputStream: OutputStream? = contentResolver.openOutputStream(imageUri)
        imgFile = File(imageUri.path)

        val compressionThread = Thread {
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
        }

        compressionThread.start()

        // Carousel 설정하기
        val carouselItems = listOf(
            CarouselItem(imageUri, R.layout.carousel_image_item),
            CarouselItem(imageUri, R.layout.carousel_linear_item)
        )
        setupCarousel(carouselItems)

        // 뒤로가기 버튼 설정
        val backButton = viewBinding.cameraButton
        backButton.setOnClickListener{
            finish()
        }

        // 전송 버튼 설정
        val sendButton: ImageButton = viewBinding.calendarButton
        sendButton.setOnClickListener {
            /**
             * 서버로 데이터 전송 로직
             */
            val birthDate = carouselAdapter.resultBirthEditText
            val groupMember = carouselAdapter.resultGroupMember

            if(birthDate == "" || birthDate == null ||  groupMember == null) {
                val builder = android.app.AlertDialog.Builder(this)

                builder.setMessage("값을 입력해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    // 그냥 팝업만 닫음.
                }
                val dialog = builder.create()
                dialog.show()
                return@setOnClickListener
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            val localDate = LocalDate.parse(birthDate, formatter)
            cameraResultViewModel.sendOcrImage(groupMember.groupMemberIndex!!, groupMember.groupMemberName!!, localDate, userInfo.userFcmToken!!, imgFile!!)

            val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            builder.setTitle("처방전 데이터 전송 완료!")
            builder.setMessage("처방전 이미지에 대한 분석이 완료되면\n해당 그룹원의 처방전으로 등록이 됩니다.\n그룹원명: ${groupMember.groupMemberName}\n복용시작일: $localDate")
            builder.setPositiveButton("확인") { _, _ ->
                // 확인 버튼 클릭 시 동작할 내용
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getMappedRectArea(bitmap: Bitmap, rectLeft: Int, rectTop: Int, rectRight: Int, rectBottom: Int): Rect {
        // 디바이스의 가로, 세로 크기 가져오기
        val displayMetrics = resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels

        // 이미지의 가로, 세로 크기 가져오기
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height

        // 이미지와 화면의 비율 계산
        val widthRatio = imageWidth.toFloat() / displayWidth.toFloat()
        val heightRatio = imageHeight.toFloat() / displayHeight.toFloat()

        // 매핑할 좌표 계산
        val mappedLeft = (rectLeft * widthRatio).toInt() + RESULT_WIDTH_WEIGHT
        val mappedTop = (rectTop * heightRatio).toInt()
        val mappedRight = (rectRight * widthRatio).toInt() - RESULT_WIDTH_WEIGHT
        val mappedBottom = (rectBottom * heightRatio).toInt()

        // 비트맵을 크롭할 영역 계산
        return Rect(mappedLeft, mappedTop, mappedRight, mappedBottom)
    }

    private fun updateIndicator(currentPosition: Int, indicatorLayout: LinearLayout) {
        for (i in 0 until indicatorLayout.childCount) {
            val indicatorView = indicatorLayout.getChildAt(i) as ImageView
            if (i == currentPosition) {
                indicatorView.setImageResource(R.drawable.indicator_active)
            } else {
                indicatorView.setImageResource(R.drawable.indicator_inactive)
            }
        }
    }

    private fun setupCarousel(carouselItems: List<CarouselItem>) {
        val indicatorLayout = viewBinding.indicatorLayout
        val viewPager = viewBinding.carouselViewPager
        carouselAdapter = CarouselAdapter(carouselItems, this)
        carouselAdapter.setGroupMemberList(groupMemberList)
        viewPager.adapter = carouselAdapter

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.offscreenPageLimit = carouselItems.size
        viewPager.setCurrentItem(0, false)


        // ViewPager2의 슬라이드 이벤트 리스너를 등록합니다
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position, indicatorLayout)
            }
        })

        // Indicator 아이템 개수와 일치하는 ImageView를 Indicator 레이아웃에 추가합니다
        for (i in carouselItems.indices) {
            val indicatorView = ImageView(this)
            indicatorView.setImageResource(R.drawable.indicator_inactive)

            // Indicator 간격 조정
            val margin = resources.getDimensionPixelSize(R.dimen.indicator_margin)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.marginEnd = margin
            indicatorView.layoutParams = layoutParams

            indicatorLayout.addView(indicatorView)
        }

        // 초기 상태로 첫 번째 Indicator를 활성화합니다
        updateIndicator(0, indicatorLayout)
    }
}

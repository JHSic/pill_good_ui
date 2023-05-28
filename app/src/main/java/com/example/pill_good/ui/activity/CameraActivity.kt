package com.example.pill_good.ui.activity

import com.example.pill_good.databinding.ActivityCameraBinding

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.widget.FrameLayout
import androidx.camera.core.ImageCaptureException
import com.example.pill_good.R
import java.text.SimpleDateFormat
import java.util.Locale


class CameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        frameLayout = viewBinding.frameLayout

        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.takePhotoButton.setOnClickListener { takePhoto() }

        val displayMetrics = resources.displayMetrics
        val aspectRatio = 1.41f // A4 문서의 폭 대 높이 비율
        val leftMarginRatio = 2
        val topMarginRatio = 5
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels
        val rectangleWidth = (deviceWidth * 0.8f).toInt() // deviceWidth의 80%
        val rectangleHeight = (rectangleWidth * aspectRatio).toInt()
        val rectangleMarginLeft = (deviceWidth - rectangleWidth) / leftMarginRatio
        val rectangleMarginTop = (deviceHeight - rectangleHeight) / topMarginRatio
        //val heightMarginGap = ((deviceHeight - rectangleHeight) / leftMarginRatio) - rectangleMarginTop


        with(viewBinding) {
            borderTop.layoutParams = FrameLayout.LayoutParams(
                deviceWidth,
                (deviceHeight - rectangleHeight) / topMarginRatio
            ).apply {
                leftMargin = 0
                topMargin = 0
            }

            borderLeft.layoutParams =
                FrameLayout.LayoutParams((deviceWidth - rectangleWidth) / leftMarginRatio, rectangleHeight)
                    .apply {
                        leftMargin = 0
                        topMargin = rectangleMarginTop
                    }

            rectangleView.layoutParams =
                FrameLayout.LayoutParams(rectangleWidth, rectangleHeight).apply {
                    leftMargin = rectangleMarginLeft
                    topMargin = rectangleMarginTop
                }

            borderRight.layoutParams =
                FrameLayout.LayoutParams((deviceWidth - rectangleWidth) / leftMarginRatio, rectangleHeight)
                    .apply {
                        leftMargin = rectangleMarginLeft + rectangleWidth
                        topMargin = (deviceHeight - rectangleHeight) / topMarginRatio
                    }

            borderBottom.layoutParams = FrameLayout.LayoutParams(
                deviceWidth,
//                (deviceHeight - rectangleHeight) / topMarginRatio
                deviceHeight - rectangleHeight
            ).apply {
                leftMargin = 0
                topMargin = rectangleMarginTop + rectangleHeight
            }

            takePhotoDescriptionView.layoutParams = FrameLayout.LayoutParams(
                deviceWidth,
                deviceHeight - (rectangleHeight + (((deviceHeight - rectangleHeight) / topMarginRatio) * 2))
            ).apply {
                leftMargin = 0
                topMargin = rectangleMarginTop + rectangleHeight
            }

            rectangleView.background =
                ContextCompat.getDrawable(this@CameraActivity, R.drawable.rectangle_border)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PillGood-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "이미지 저장에 성공했습니다! 경로: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val intent = Intent(this@CameraActivity, CameraResultActivity::class.java)
                    intent.putExtra("savedUri", output.savedUri.toString())
                    intent.putExtra("rectangleLeft", viewBinding.rectangleView.left)
                    intent.putExtra("rectangleTop", viewBinding.rectangleView.top)
                    intent.putExtra("rectangleRight", viewBinding.rectangleView.right)
                    intent.putExtra("rectangleBottom", viewBinding.rectangleView.bottom)
                    intent.putExtra("frameWidth", viewBinding.frameLayout.width)
                    intent.putExtra("frameHeight", viewBinding.frameLayout.height)
                    startActivity(intent)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                // Used to bind the lifecycle of cameras to the lifecycle owner
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
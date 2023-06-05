package com.example.pill_good.data.remote

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.pill_good.R
import com.example.pill_good.data.dto.EditOCRDTO
import com.example.pill_good.data.dto.PillScheduleDTO
import com.example.pill_good.ui.activity.NotificationActivity
import com.example.pill_good.ui.activity.EditOCRActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        // 수신한 메시지 처리
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            val firstKey = data.keys.firstOrNull()
            val groupMemberIndexValue = data[firstKey]

            if (firstKey == "그룹원 인덱스") {
                // 처방전 처리
                processPrescriptionNotification(data, title!!, body!!)
            } else if (firstKey == "그룹원 전화번호") {
                // 복약 알림 처리
                processMedicationNotification(data, title!!, body!!)
            }
        }
    }

    private fun processMedicationNotification(data : Map<String, String>, title : String, content : String) {
        // 전화번호 010-xxxx-xxxx 형식에서 010xxxxxxxx 형식으로 변환
        val phoneNumber = data["그룹원 전화번호"]?.replace("-", "")

        // SMS 발송을 권한에 따라 처리
        if (checkSmsPermission()) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, content, null, null)
        }
        // 화면 이동 설정
        var intent = Intent(this, NotificationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        generateNotificationToDevice(title, content, pendingIntent)
    }

    private fun processPrescriptionNotification(data : Map<String, String>, title : String, content : String) {
        // 처방전 수정 알림 처리 로직 구현

        val groupMemberName = data["그룹원 이름"] // "김현태"
        val hospitalName = data["병원 이름"] // "구미병원"
        val hospitalPhone = data["병원 전화번호"] // "(054)-123-4567"
        val diseaseCode = data["질병 코드"] // "R05"
        val pillListJson = data["약 정보"]
        val pillList = JSONArray(pillListJson)

        val pillDataList = ArrayList<PillScheduleDTO>()

        for (i in 0 until pillList.length()) {
            val pillObject = pillList.getJSONObject(i)

            val pillName = pillObject.getString("pillName")
            val takeCount = pillObject.getInt("takeCount")
            val takeDay = pillObject.getInt("takeDay")
            val takePillTimeList = pillObject.getJSONArray("takePillTimeList")

            // JSONArray를 List<Int>로 변환
            val pillTimeList = mutableListOf<Int>()
            for (j in 0 until takePillTimeList.length()) {
                pillTimeList.add(takePillTimeList.getInt(j))
            }

            // PillData 객체 생성
            val pillData = PillScheduleDTO(pillName, takeCount, takeDay, pillTimeList)

            // PillData 객체를 리스트에 추가
            pillDataList.add(pillData)
        }

        // prescriptionEditActivity
        var intent = Intent(this, EditOCRActivity::class.java)
        val prescriptionData = EditOCRDTO(
            groupMemberName = groupMemberName,
            hospitalName = hospitalName,
            phoneNumber = hospitalPhone,
            diseaseCode = diseaseCode,
            pillList = pillDataList
        )
        intent.putExtra("prescriptionData", prescriptionData)
        val pendingIntent = PendingIntent.getActivity(applicationContext, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // 알림을 표시하거나 필요한 작업 수행
        generateNotificationToDevice(title, content, pendingIntent)
    }

    private fun generateNotificationToDevice(title : String, content : String, pendingIntent : PendingIntent){
        val channelId = "default"

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_logo_test)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt() // 현재 시간 기준 고유한 notificationId 설정
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun checkSmsPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        return result == PackageManager.PERMISSION_GRANTED
    }
}

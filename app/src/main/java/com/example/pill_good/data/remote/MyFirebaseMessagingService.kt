package com.example.pill_good.data.remote

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pill_good.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.json.JsonNull.content
import org.json.JSONArray
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        // 수신한 메시지 처리
        if (remoteMessage.data.isNotEmpty()) {
            // 데이터 페이로드가 있는 경우 처리 -> 처방전에만 데이터 페이로드 존재
            val data = remoteMessage.data
            processPrescriptionNotification(data, title!!, body!!)
        }
        else {
            // 데이터 페이로드가 없는 경우 처리 -> 일반적인 복약알림
            processMedicationNotification(title!!, body!!)
        }

    }

    private fun processMedicationNotification(title: String, content: String) {
        // 복약 알림 처리 로직 구현

        // 알림을 표시하거나 필요한 작업 수행
    }

    private fun processPrescriptionNotification(data: Map<String, String>, title: String, content: String) {
        // 처방전 수정 알림 처리 로직 구현

        val groupMemberName = data["그룹원 이름"] // "김현태"
        val startDate = data["복용 시작 날짜"] // "2020-11-20"
        val hospitalName = data["병원 이름"] // "구미병원"
        val phoneNumber = data["병원 전화번호"] // "(054)-123-4567"
        val diseaseCode = data["질병 코드"] // "R05"
        val pillListJson = data["약 정보"]
        val pillList = JSONArray(pillListJson)

        for (i in 0 until pillList.length()) {
            val pillObject = pillList.getJSONObject(i)

            val pillName = pillObject.getString("pillName")
            val takeCount = pillObject.getString("takeCount")
            val takeDay = pillObject.getInt("takeDay")
            val takePillTimeList = pillObject.getJSONArray("takePillTimeList")

            // 추출한 값 활용 예시
            println("약 이름: $pillName")
            println("복용 횟수: $takeCount")
            println("복용 일수: $takeDay")

            // 복용 시간 목록 추출
            val pillTimeList = mutableListOf<Int>()
            for (j in 0 until takePillTimeList.length()) {
                pillTimeList.add(takePillTimeList.getInt(j))
            }
            println("복용 시간 목록: $pillTimeList")
        }

        // prescriptionEditActivity
        var intent = Intent(this, )

        // 알림을 표시하거나 필요한 작업 수행
        generateNotificationToDevice(title, content, )
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
}

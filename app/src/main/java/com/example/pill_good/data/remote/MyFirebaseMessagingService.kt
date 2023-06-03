package com.example.pill_good.data.remote

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pill_good.R
import com.example.pill_good.data.model.PillData
import com.example.pill_good.data.model.PrescriptionData
import com.example.pill_good.ui.activity.NotificationActivity
import com.example.pill_good.ui.activity.PrescriptionEditActivity
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
        var intent = Intent(this, NotificationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        generateNotificationToDevice(title, content, pendingIntent)
    }

    private fun processPrescriptionNotification(data: Map<String, String>, title: String, content: String) {
        // 처방전 수정 알림 처리 로직 구현

        val groupMemberName = data["그룹원 이름"] // "김현태"
        val hospitalName = data["병원 이름"] // "구미병원"
        val hospitalPhone = data["병원 전화번호"] // "(054)-123-4567"
        val diseaseCode = data["질병 코드"] // "R05"
        val pillListJson = data["약 정보"]
        val pillList = JSONArray(pillListJson)

        val pillDataList = ArrayList<PillData>()

        for (i in 0 until pillList.length()) {
            val pillObject = pillList.getJSONObject(i)

            val pillName = pillObject.getString("pillName")
            val takeCount = pillObject.getString("takeCount")
            val takeDay = pillObject.getString("takeDay")
            val takePillTimeList = pillObject.getJSONArray("takePillTimeList")

            // JSONArray를 List<Int>로 변환
            val pillTimeList = mutableListOf<Int>()
            for (j in 0 until takePillTimeList.length()) {
                pillTimeList.add(takePillTimeList.getInt(j))
            }

            // PillData 객체 생성
            val pillData = PillData(pillName, takeCount, takeDay, pillTimeList)

            // PillData 객체를 리스트에 추가
            pillDataList.add(pillData)
        }

        // prescriptionEditActivity
        var intent = Intent(this, PrescriptionEditActivity::class.java)
        val prescriptionData = PrescriptionData(
            groupMemberName = groupMemberName,
            hospitalName = hospitalName,
            hospitalPhone = hospitalPhone,
            diseaseCode = diseaseCode,
            pillDataList = pillDataList
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
}

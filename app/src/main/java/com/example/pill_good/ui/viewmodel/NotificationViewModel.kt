package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.NotificationDTO
import com.example.pill_good.repository.NotificationRepositoryImpl
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationViewModel(private val notificationRepository : NotificationRepositoryImpl) : ViewModel() {
    private val _notificationData = MutableLiveData<List<NotificationDTO>>()
    val notificationData : LiveData<List<NotificationDTO>> get() = _notificationData

    fun loadNotificationData() { // user_index 받아야함
        viewModelScope.launch {
            try {
//            _notification.value = notificaitonRepository.readByUserId(userId)
                test()
            } catch (e: Exception) {
                // 예외 처리
            }
        }
    }

    private fun test(){
        val notificationList = mutableListOf<NotificationDTO>()

        for (i in 1..10) {
            val notificationTime = LocalDateTime.now().minusMinutes(10 * i.toLong())

            val notification = NotificationDTO(
                notificationIndex = i.toLong(),
                notificationContent = "Notification $i",
                notificationTime = notificationTime,
                notificationCheck = false
            )
            notificationList.add(notification)
        }

        _notificationData.value = notificationList
    }
}
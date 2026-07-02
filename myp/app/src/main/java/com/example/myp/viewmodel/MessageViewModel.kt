package com.example.myp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myp.data.api.RetrofitClient
import com.example.myp.data.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _sendResult = MutableLiveData<Boolean>()
    val sendResult: LiveData<Boolean> = _sendResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadMessages(coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = RetrofitClient.apiService.getAllMessages(coupleId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _messages.postValue(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun sendMessage(coupleId: Long, senderId: Long, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "coupleId" to coupleId,
                    "senderId" to senderId,
                    "content" to content
                )
                val response = RetrofitClient.apiService.sendMessage(body).execute()
                _sendResult.postValue(response.isSuccessful)
                if (response.isSuccessful) {
                    loadMessages(coupleId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _sendResult.postValue(false)
            }
        }
    }

    fun refreshMessages(coupleId: Long) {
        loadMessages(coupleId)
    }
}
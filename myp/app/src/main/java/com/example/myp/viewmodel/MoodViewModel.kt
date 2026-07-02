package com.example.myp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myp.data.api.RetrofitClient
import com.example.myp.data.model.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {

    private val _todayMoods = MutableLiveData<List<Mood>>()
    val todayMoods: LiveData<List<Mood>> = _todayMoods

    private val _allMoods = MutableLiveData<List<Mood>>()
    val allMoods: LiveData<List<Mood>> = _allMoods

    private val _setResult = MutableLiveData<Map<String, Any>>()
    val setResult: LiveData<Map<String, Any>> = _setResult

    fun loadTodayMoods(coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTodayMoods(coupleId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _todayMoods.postValue(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadAllMoods(coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getAllMoods(coupleId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _allMoods.postValue(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setMood(userId: Long, coupleId: Long, moodType: Int, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "userId" to userId,
                    "coupleId" to coupleId,
                    "moodType" to moodType,
                    "message" to message
                )
                val response = RetrofitClient.apiService.setMood(body).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _setResult.postValue(it)
                        loadTodayMoods(coupleId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
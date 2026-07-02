package com.example.myp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myp.data.api.RetrofitClient
import com.example.myp.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos

    private val _uploadResult = MutableLiveData<Map<String, Any>>()
    val uploadResult: LiveData<Map<String, Any>> = _uploadResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteResult = MutableLiveData<Pair<Boolean, String>>()
    val deleteResult: LiveData<Pair<Boolean, String>> = _deleteResult

    fun loadPhotos(coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = RetrofitClient.apiService.getAllPhotos(coupleId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _photos.postValue(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun likePhoto(photoId: Long, currentLikes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.likePhoto(photoId).execute()
                if (response.isSuccessful) {
                    val currentPhotos = _photos.value ?: emptyList()
                    _photos.postValue(currentPhotos.map {
                        if (it.id == photoId) {
                            it.copy(likes = currentLikes + 1)
                        } else {
                            it
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshPhotos(coupleId: Long) {
        loadPhotos(coupleId)
    }

    fun deletePhoto(photoId: Long, userId: Long, coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.deletePhoto(photoId, userId).execute()
                if (response.isSuccessful) {
                    val result = response.body()
                    val success = result?.get("success") as? Boolean ?: false
                    val message = result?.get("message") as? String ?: "删除失败"

                    _deleteResult.postValue(Pair(success, message))

                    if (success) {
                        // 从列表中移除已删除的照片
                        val currentPhotos = _photos.value ?: emptyList()
                        _photos.postValue(currentPhotos.filter { it.id != photoId })
                    }
                } else {
                    _deleteResult.postValue(Pair(false, "删除失败"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _deleteResult.postValue(Pair(false, "网络错误"))
            }
        }
    }
}
package com.example.myp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myp.data.api.RetrofitClient
import com.example.myp.data.model.Location
import com.example.myp.data.model.LocationStatus
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationViewModel : ViewModel() {

    private val _myLocation = MutableLiveData<Location?>()
    val myLocation: LiveData<Location?> = _myLocation

    private val _partnerLocation = MutableLiveData<Location?>()
    val partnerLocation: LiveData<Location?> = _partnerLocation

    private val _locationStatus = MutableLiveData<LocationStatus?>()
    val locationStatus: LiveData<LocationStatus?> = _locationStatus

    private val _updateResult = MutableLiveData<Map<String, Any>?>()
    val updateResult: LiveData<Map<String, Any>?> = _updateResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * 更新我的位置
     */
    fun updateMyLocation(
        userId: Long,
        coupleId: Long,
        latitude: Double,
        longitude: Double,
        address: String?
    ) {
        val request = mapOf(
            "userId" to userId,
            "coupleId" to coupleId,
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to (address ?: "")
        )

        RetrofitClient.apiService.updateLocation(request).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result["success"] == true) {
                        _updateResult.postValue(result)
                        // 更新本地位置
                        val locationData = result["location"] as? Map<*, *>
                        if (locationData != null) {
                            _myLocation.postValue(parseLocation(locationData))
                        }
                    } else {
                        _errorMessage.postValue(result?.get("message") as? String ?: "更新失败")
                    }
                } else {
                    _errorMessage.postValue("网络错误")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("LocationViewModel", "更新位置失败", t)
                _errorMessage.postValue("网络连接失败")
            }
        })
    }

    /**
     * 获取对方位置
     */
    fun getPartnerLocation(userId: Long, coupleId: Long) {
        RetrofitClient.apiService.getPartnerLocation(userId, coupleId)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result["success"] == true) {
                            val locationData = result["location"] as? Map<*, *>
                            if (locationData != null) {
                                _partnerLocation.postValue(parseLocation(locationData))
                            }
                        } else {
                            _partnerLocation.postValue(null)
                            // 不显示错误消息，让 UI 自己处理显示
                        }
                    } else {
                        _partnerLocation.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Log.e("LocationViewModel", "获取对方位置失败", t)
                    _errorMessage.postValue("网络连接失败")
                }
            })
    }

    /**
     * 切换位置共享开关
     */
    fun toggleSharing(userId: Long, coupleId: Long, enabled: Boolean) {
        val request = mapOf(
            "userId" to userId,
            "coupleId" to coupleId,
            "enabled" to enabled
        )

        RetrofitClient.apiService.toggleLocationSharing(request)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result["success"] == true) {
                            // 更新状态
                            getLocationStatus(userId, coupleId)
                        } else {
                            _errorMessage.postValue(result?.get("message") as? String ?: "操作失败")
                        }
                    } else {
                        _errorMessage.postValue("网络错误")
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Log.e("LocationViewModel", "切换共享失败", t)
                    _errorMessage.postValue("网络连接失败")
                }
            })
    }

    /**
     * 获取位置共享状态
     */
    fun getLocationStatus(userId: Long, coupleId: Long) {
        RetrofitClient.apiService.getLocationStatus(userId, coupleId)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result["success"] == true) {
                            val status = LocationStatus(
                                sharingEnabled = result["sharingEnabled"] as? Boolean ?: false,
                                hasLocation = result["hasLocation"] as? Boolean ?: false,
                                updatedAt = result["updatedAt"] as? String
                            )
                            _locationStatus.postValue(status)
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Log.e("LocationViewModel", "获取状态失败", t)
                }
            })
    }

    private fun parseLocation(data: Map<*, *>): Location {
        return Location(
            userId = (data["userId"] as? Number)?.toLong() ?: 0L,
            latitude = (data["latitude"] as? Number)?.toDouble() ?: 0.0,
            longitude = (data["longitude"] as? Number)?.toDouble() ?: 0.0,
            address = data["address"] as? String,
            updatedAt = data["updatedAt"] as? String ?: ""
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

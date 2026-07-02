package com.example.myp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myp.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Map<String, Any>>()
    val loginResult: LiveData<Map<String, Any>> = _loginResult

    private val _registerResult = MutableLiveData<Map<String, Any>>()
    val registerResult: LiveData<Map<String, Any>> = _registerResult

    private val _joinCoupleResult = MutableLiveData<Map<String, Any>>()
    val joinCoupleResult: LiveData<Map<String, Any>> = _joinCoupleResult

    private val _createCoupleResult = MutableLiveData<Map<String, Any>>()
    val createCoupleResult: LiveData<Map<String, Any>> = _createCoupleResult

    private val _coupleStatusResult = MutableLiveData<Map<String, Any>>()
    val coupleStatusResult: LiveData<Map<String, Any>> = _coupleStatusResult

    private val _sendCodeResult = MutableLiveData<Map<String, Any>>()
    val sendCodeResult: LiveData<Map<String, Any>> = _sendCodeResult

    private var _currentUser: Map<String, Any>? = null
    val currentUser: Map<String, Any>? get() = _currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "email" to email,
                    "password" to password
                )
                val response = RetrofitClient.apiService.login(body).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _currentUser = result
                    _loginResult.postValue(result.toMutableMap()) // 确保是新对象
                } else {
                    _loginResult.postValue(
                        mutableMapOf(
                            "success" to false,
                            "message" to (result?.get("message") as? String ?: "登录失败（${response.code()}）"),
                            "timestamp" to System.currentTimeMillis() // 添加时间戳确保唯一
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginResult.postValue(
                    mutableMapOf(
                        "success" to false,
                        "message" to "网络连接失败：${e.message ?: "未知错误"}",
                        "timestamp" to System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun register(email: String, password: String, nickname: String, isMale: Boolean, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "email" to email,
                    "password" to password,
                    "nickname" to nickname,
                    "isMale" to isMale,
                    "code" to code
                )
                val response = RetrofitClient.apiService.register(body).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _registerResult.postValue(result)
                } else {
                    _registerResult.postValue(
                        mapOf(
                            "success" to false,
                            "message" to (result?.get("message") as? String ?: "注册失败（${response.code()}）")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _registerResult.postValue(
                    mapOf("success" to false, "message" to "网络连接失败：${e.message ?: "未知错误"}")
                )
            }
        }
    }

    fun joinCouple(userId: Long, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "userId" to userId,
                    "code" to code
                )
                val response = RetrofitClient.apiService.joinCouple(body).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _joinCoupleResult.postValue(result)
                } else {
                    _joinCoupleResult.postValue(
                        mapOf(
                            "success" to false,
                            "message" to (result?.get("message") as? String ?: "加入失败（${response.code()}）")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _joinCoupleResult.postValue(
                    mapOf("success" to false, "message" to "网络连接失败，请检查手机和电脑是否在同一 WiFi")
                )
            }
        }
    }

    fun setCurrentUser(user: Map<String, Any>) {
        _currentUser = user
    }

    fun createCouple(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "userId" to userId
                )
                val response = RetrofitClient.apiService.createCouple(body).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _createCoupleResult.postValue(result)
                } else {
                    _createCoupleResult.postValue(
                        mapOf(
                            "success" to false,
                            "message" to (result?.get("message") as? String ?: "创建失败（${response.code()}）")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _createCoupleResult.postValue(
                    mapOf("success" to false, "message" to "网络连接失败，请检查手机和电脑是否在同一 WiFi")
                )
            }
        }
    }

    fun checkCoupleStatus(coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.coupleStatus(coupleId).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _coupleStatusResult.postValue(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendCode(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf("email" to email)
                val response = RetrofitClient.apiService.sendCode(body).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _sendCodeResult.postValue(result)
                } else {
                    _sendCodeResult.postValue(
                        mapOf(
                            "success" to false,
                            "message" to (result?.get("message") as? String ?: "验证码发送失败（${response.code()}）")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _sendCodeResult.postValue(
                    mapOf("success" to false, "message" to "网络连接失败：${e.message ?: "未知错误"}")
                )
            }
        }
    }
}
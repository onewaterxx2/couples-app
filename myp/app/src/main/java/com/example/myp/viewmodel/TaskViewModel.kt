package com.example.myp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myp.data.api.RetrofitClient
import com.example.myp.data.model.CoupleTask
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<CoupleTask>>()
    val tasks: LiveData<List<CoupleTask>> = _tasks

    private val _operationResult = MutableLiveData<Map<String, Any>>()
    val operationResult: LiveData<Map<String, Any>> = _operationResult

    fun loadTasks(coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTasks(coupleId).execute()
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val tasksData = result["tasks"] as? List<*>
                    if (tasksData != null) {
                        val gson = Gson()
                        val taskList = tasksData.mapNotNull { taskMap ->
                            try {
                                val json = gson.toJson(taskMap)
                                gson.fromJson(json, CoupleTask::class.java)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _tasks.postValue(taskList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createTask(coupleId: Long, creatorId: Long, title: String, description: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mutableMapOf<String, Any>(
                    "coupleId" to coupleId,
                    "creatorId" to creatorId,
                    "title" to title
                )
                if (!description.isNullOrBlank()) {
                    body["description"] = description
                }

                val response = RetrofitClient.apiService.createTask(body).execute()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    _operationResult.postValue(result)
                    loadTasks(coupleId) // 刷新列表
                } else {
                    _operationResult.postValue(
                        mapOf(
                            "success" to false,
                            "message" to "创建失败（${response.code()}）"
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _operationResult.postValue(
                    mapOf("success" to false, "message" to "网络错误：${e.message}")
                )
            }
        }
    }

    fun toggleTask(taskId: Long, coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf("taskId" to taskId)
                val response = RetrofitClient.apiService.toggleTask(body).execute()
                if (response.isSuccessful) {
                    loadTasks(coupleId) // 刷新列表
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTask(taskId: Long, coupleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.deleteTask(taskId).execute()
                if (response.isSuccessful) {
                    loadTasks(coupleId) // 刷新列表
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

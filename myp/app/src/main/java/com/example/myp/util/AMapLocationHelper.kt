package com.example.myp.util

import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

class AMapLocationHelper(private val context: Context) {

    private var locationClient: AMapLocationClient? = null
    private var locationListener: ((latitude: Double, longitude: Double, address: String) -> Unit)? = null

    fun startLocation(onLocationUpdate: (latitude: Double, longitude: Double, address: String) -> Unit) {
        this.locationListener = onLocationUpdate

        // 初始化定位
        try {
            AMapLocationClient.updatePrivacyShow(context, true, true)
            AMapLocationClient.updatePrivacyAgree(context, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        locationClient = AMapLocationClient(context)

        // 设置定位参数
        val locationOption = AMapLocationClientOption().apply {
            // 设置定位模式为高精度模式
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            // 设置定位间隔，单位毫秒，默认为2000ms
            interval = 1000
            // 设置是否返回地址信息
            isNeedAddress = true
            // 设置是否只定位一次
            isOnceLocation = false
            // 设置是否允许模拟位置
            isMockEnable = false
            // 设置定位超时时间
            httpTimeOut = 20000
        }

        locationClient?.setLocationOption(locationOption)

        // 设置定位监听
        locationClient?.setLocationListener { aMapLocation ->
            if (aMapLocation != null && aMapLocation.errorCode == 0) {
                // 定位成功
                val latitude = aMapLocation.latitude
                val longitude = aMapLocation.longitude
                val address = aMapLocation.address ?: aMapLocation.city ?: "未知位置"

                locationListener?.invoke(latitude, longitude, address)
            } else {
                // 定位失败
                val errorInfo = "定位失败: ${aMapLocation?.errorCode} - ${aMapLocation?.errorInfo}"
                android.util.Log.e("AMapLocation", errorInfo)
            }
        }

        // 启动定位
        locationClient?.startLocation()
    }

    fun stopLocation() {
        locationClient?.stopLocation()
        locationClient?.onDestroy()
        locationClient = null
    }
}

package com.ke.music.app.data.api

import com.ke.music.app.BuildConfig
import com.ke.music.app.data.model.BaseListVO
import com.ke.music.app.data.model.BaseVO

/**
 * 统一处理网络请求异常
 */
suspend fun <T> safeApiCall(call: suspend () -> BaseVO<T>): BaseVO<T> {
    return try {
        call()
    } catch (e: Exception) {
        if(BuildConfig.DEBUG){
            e.printStackTrace()
        }
        BaseVO(code = -1, success = false, message = e.localizedMessage ?: "未知错误")
    }
}



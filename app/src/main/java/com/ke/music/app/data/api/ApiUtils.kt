package com.ke.music.app.data.api

import com.ke.music.app.data.model.BaseVO

/**
 * 统一处理网络请求异常
 */
suspend fun <T> safeApiCall(call: suspend () -> BaseVO<T>): BaseVO<T> {
    return try {
        call()
    } catch (e: Exception) {
        BaseVO(code = -1, success = false, message = e.localizedMessage ?: "未知错误")
    }
}

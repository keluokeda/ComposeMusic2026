package com.ke.music.app.data.store

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataStore @Inject constructor() {
    private val mmkv = MMKV.defaultMMKV()

    var token: String?
        get() = mmkv.getString("token", null)
        set(value) {
            mmkv.encode("token", value)
        }
}
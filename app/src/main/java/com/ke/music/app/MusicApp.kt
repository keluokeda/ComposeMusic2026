package com.ke.music.app

import android.app.Application
import android.widget.Toast
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        _instance = this

        Logger.addLogAdapter(object :AndroidLogAdapter(){
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        MMKV.initialize(this)
    }

    companion object {
        private var _instance: MusicApp? = null

        fun toast(message: String) {
            _instance?.run {
                Toast.makeText(
                    this, message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

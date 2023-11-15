package com.pgleqi.service

import com.pgleqi.constant.gson
import com.pgleqi.model.AppSettings

object AppService {
    private lateinit var appSettings: AppSettings
    fun loadAppSettings() {
        appSettings = FileService.readTextFile(FileService.APP_SETTINGS_FILE_PATH)?.let { json ->
            gson.fromJson(json, AppSettings::class.java)
        } ?: AppSettings()
        println(appSettings)
    }
}